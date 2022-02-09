/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.contabilidad.sfccbdmq;

import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
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
import javax.faces.model.SelectItem;
import org.beans.sfccbdmq.CcostoFacade;
import org.entidades.sfccbdmq.Ccosto;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "ccostoSfccbdmq")
@ViewScoped
public class CcostoBean {

    /**
     * Creates a new instance of CcostoBean
     */
    public CcostoBean() {
    }
    private Ccosto centro;
    private List<Ccosto> centros;
    private Formulario formulario = new Formulario();
    private Perfiles perfil;
    @EJB
    private CcostoFacade ejbCcosto;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    /**
     * @return the centro
     */
    public Ccosto getCentro() {
        return centro;
    }

    /**
     * @param centro the centro to set
     */
    public void setCentro(Ccosto centro) {
        this.centro = centro;
    }

    /**
     * @return the centros
     */
    public List<Ccosto> getCcosto() {
        return centros;
    }

    /**
     * @param centros the centros to set
     */
    public void setCcosto(List<Ccosto> centros) {
        this.centros = centros;
    }

    public String buscar() {
       
        Map parametros = new HashMap();
        parametros.put(";orden", "o.codigo desc");
        try {
            centros = ejbCcosto.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CcostoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String crear() {
       
        formulario.insertar();
        centro = new Ccosto();
        return null;
    }

    public String modificar(Ccosto centro) {

        this.centro = centro;
        formulario.editar();

        return null;
    }
    public String eliminar(Ccosto centro) {

        this.centro = centro;
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

        if ((centro.getCodigo()== null) || (centro.getCodigo().isEmpty())) {
            MensajesErrores.advertencia("Es necesario código de centro");
            return true;
        }
        if ((centro.getNombre()== null) || (centro.getNombre().isEmpty())) {
            MensajesErrores.advertencia("Es necesario nombre de centro");
            return true;
        }
        
//        if ((centro.getRelacioncobranza()== null) || (centro.getRelacioncobranza()<=0)) {
//            MensajesErrores.advertencia("Es necesario Relación de cobranza válido en centro");
//            return true;
//        }
        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            // buscar si existe clasiificador ya con esa fuente en ese proyecto
            ejbCcosto.create(centro, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CcostoBean.class.getName()).log(Level.SEVERE, null, ex);
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

            ejbCcosto.edit(centro, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CcostoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public String borrar() {
        try {
            // buscar que no existan reformas,certificaciones para poder borrar

            ejbCcosto.remove(centro, seguridadbean.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CcostoBean.class.getName()).log(Level.SEVERE, null, ex);
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

    public Ccosto traer(Integer id) throws ConsultarException {
        return ejbCcosto.find(id);
    }

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "CcostoVista";
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
    public SelectItem[] getComboCcosto(){
        
        Map parametros = new HashMap();
        parametros.put(";orden", "o.codigo desc");
        try {
            return Combos.getSelectItems(ejbCcosto.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CcostoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public SelectItem[] getComboCcostoStr(){
        
        Map parametros = new HashMap();
        parametros.put(";orden", "o.codigo desc");
        try {
        List<Ccosto> lcc=ejbCcosto.encontarParametros(parametros);
        int size =lcc.size() + 1;
        SelectItem[] items = new SelectItem[size];
        int i = 0;
        
            items[0] = new SelectItem(null, "--- Seleccione uno ---");
            i++;
        
        
        
        for (Ccosto x : lcc) {
            items[i++] = new SelectItem(x.getCodigo(), x.toString());
        }
        return items;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CcostoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public String traerNombre(String codigo){
        Map parametros = new HashMap();
        parametros.put(";where", "o.codigo=:codigo");
        parametros.put("codigo", codigo);
        try {
            List<Ccosto> listaCentros = ejbCcosto.encontarParametros(parametros);
            for (Ccosto c:listaCentros){
                return c.getNombre();
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CcostoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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
