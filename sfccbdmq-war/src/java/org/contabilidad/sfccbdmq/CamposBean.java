/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.contabilidad.sfccbdmq;

import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Auxiliar;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.util.HashMap;
import java.util.LinkedList;
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
import org.beans.sfccbdmq.CamposFacade;
import org.entidades.sfccbdmq.Campos;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "camposSfccbdmq")
@ViewScoped
public class CamposBean {

    /**
     * Creates a new instance of CamposBean
     */
    public CamposBean() {
    }
    private Campos campo;
    private List<Campos> campos;
    private List<Auxiliar> listaCampos;
    private Formulario formulario = new Formulario();
    private Perfiles perfil;
    @EJB
    private CamposFacade ejbCampos;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
//    @ManagedProperty(value = "#{entidadesSfccbdmq}")
//    private EntidadesBean entidadesBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    /**
     * @return the campo
     */
    public Campos getCampo() {
        return campo;
    }

    /**
     * @param campo the campo to set
     */
    public void setCampo(Campos campo) {
        this.campo=campo;
    }

    /**
     * @return the 
     */
    public List<Campos> getCampos() {
        return campos;
    }

    /**
     * @param campos the campos to set
     */
    public void setCampos(List<Campos> campos) {
        this.campos = campos;
    }

    public String buscar() {
        Map parametros = new HashMap();
        parametros.put(";orden", "o.nombre asc");
        try {
            campos = ejbCampos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CamposBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String crear() {
       
        formulario.insertar();
        cuentasBean.setCodigo(null);
        cuentasBean.setCuenta(null);
        setCampo(new Campos());
        listaCampos=new LinkedList<>();
        return null;
    }

    public String modificar(Campos campo) {

        this.setCampo(campo);
        cuentasBean.setCodigo(null);
        cuentasBean.setCuenta(null);
        formulario.editar();
        listaCampos=new LinkedList<>();
        String[] aux=campo.getCuenta().split("#");
        for (int i=0;i<=aux.length-1;i++){
            Auxiliar a=new Auxiliar();
            a.setValor(aux[i]);
            listaCampos.add(a);
        }
        return null;
    }
    public String eliminar(Campos campo) {

        this.setCampo(campo);
        cuentasBean.setCodigo(null);
        cuentasBean.setCuenta(null);
        formulario.eliminar();
        listaCampos=new LinkedList<>();
        String[] aux=campo.getCuenta().split("#");
        for (int i=0;i<=aux.length-1;i++){
            Auxiliar a=new Auxiliar();
            a.setValor(aux[i]);
            listaCampos.add(a);
        }
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

        
        if ((getCampo().getNombre()== null) || (getCampo().getNombre().isEmpty())) {
            MensajesErrores.advertencia("Es necesario nombre de campo");
            return true;
        }
        if (listaCampos.isEmpty()){
            MensajesErrores.advertencia("Es necesario cuenta en campo");
            return true;
        }
        
        return false;
    }
    public String agregar() {
        if (cuentasBean.getCuenta()==null){
            return null;
        }
        Auxiliar a=new Auxiliar();
        a.setValor(cuentasBean.getCuenta().getCodigo());
        listaCampos.add(a);
        return null;
    }
//    public void borrarCampo(ActionEvent event){
//        int fila=0;
//        
//        for (Auxiliar c:listaCampos ){
//            if (c.getValor().equals(c1.getValor())){
//                listaCampos.remove(fila);
//                return;
//            }
//            fila++;
//        }
//    } 
    public String retirar(Auxiliar c1) {
        int fila=0;
        
        for (Auxiliar c:listaCampos ){
            if (c.getValor().equals(c1.getValor())){
                listaCampos.remove(fila);
                return null;
            }
            fila++;
        }
//        listaCampos.remove(fila);
        return null;
    }
    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            // buscar si existe clasiificador ya con esa fuente en ese proyecto
            String cuenta="";
            for (Auxiliar a:listaCampos){
                if (!cuenta.isEmpty()){
                   cuenta+="#"; 
                }
                cuenta+=a.getValor();
            }
            campo.setCuenta(cuenta);
            ejbCampos.create(getCampo(), seguridadbean.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CamposBean.class.getName()).log(Level.SEVERE, null, ex);
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
            String cuenta="";
            for (Auxiliar a:listaCampos){
                if (!cuenta.isEmpty()){
                   cuenta+="#"; 
                }
                cuenta+=a.getValor();
            }
            campo.setCuenta(cuenta);
            ejbCampos.edit(getCampo(), seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CamposBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public String borrar() {
        try {
            // buscar que no existan reformas,certificaciones para poder borrar

            ejbCampos.remove(getCampo(), seguridadbean.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CamposBean.class.getName()).log(Level.SEVERE, null, ex);
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

    public Campos traer(Integer id) throws ConsultarException {
        return ejbCampos.find(id);
    }

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "CamposVista";
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

    public SelectItem[] getComboCampos(){
        
        Map parametros = new HashMap();
        parametros.put(";orden", "o.nombre asc");
        try {
            return Combos.getSelectItems(ejbCampos.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CamposBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    

    /**
     * @return the cuentasBean
     */
    public CuentasBean getCuentasBean() {
        return cuentasBean;
    }

    /**
     * @param cuentasBean the cuentasBean to set
     */
    public void setCuentasBean(CuentasBean cuentasBean) {
        this.cuentasBean = cuentasBean;
    }

    /**
     * @return the listaCampos
     */
    public List<Auxiliar> getListaCampos() {
        return listaCampos;
    }

    /**
     * @param listaCampos the listaCampos to set
     */
    public void setListaCampos(List<Auxiliar> listaCampos) {
        this.listaCampos = listaCampos;
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
