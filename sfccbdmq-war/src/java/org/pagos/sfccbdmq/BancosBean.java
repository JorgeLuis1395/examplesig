/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pagos.sfccbdmq;

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
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.BancosFacade;
import org.contabilidad.sfccbdmq.CuentasBean;
import org.entidades.sfccbdmq.Bancos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "bancosSfccbdmq")
@ViewScoped
public class BancosBean {

    /**
     * Creates a new instance of BancosBean
     */
    public BancosBean() {
    }
    private Bancos banco;
    private List<Bancos> bancos;
    private Formulario formulario = new Formulario();
    private Perfiles perfil;
    @EJB
    private BancosFacade ejbBancos;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
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
     * @return the banco
     */
    public Bancos getBanco() {
        return banco;
    }

    /**
     * @param banco the banco to set
     */
    public void setBanco(Bancos banco) {
        this.banco = banco;
    }

    /**
     * @return the bancos
     */
    public List<Bancos> getBancos() {
        return bancos;
    }

    /**
     * @param bancos the bancos to set
     */
    public void setBancos(List<Bancos> bancos) {
        this.bancos = bancos;
    }

    public String buscar() {

        Map parametros = new HashMap();
        parametros.put(";orden", "o.nombre asc");
        try {
            bancos = ejbBancos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(BancosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String crear() {

        formulario.insertar();
        banco = new Bancos();
        cuentasBean.setCuenta(null);
        cuentasBean.setCodigo(null);
        return null;
    }

    public String modificar(Bancos banco) {

        this.banco = banco;
        formulario.editar();
        cuentasBean.setCuenta(cuentasBean.traerCodigo(banco.getCuenta()));
        cuentasBean.setCodigo(banco.getCuenta());
        return null;
    }

    public String eliminar(Bancos banco) {

        this.banco = banco;
        formulario.eliminar();
        cuentasBean.setCuenta(cuentasBean.traerCodigo(banco.getCuenta()));
        cuentasBean.setCodigo(banco.getCuenta());
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

        if ((banco.getNombre() == null) || (banco.getNombre().isEmpty())) {
            MensajesErrores.advertencia("Es necesario nombre de banco");
            return true;
        }
        if ((banco.getNumerocuenta() == null) || (banco.getNumerocuenta().isEmpty())) {
            MensajesErrores.advertencia("Es necesario número de cuenta de banco");
            return true;
        }
        Cuentas cuentaBanco = cuentasBean.traerCodigo(banco.getCuenta());
        cuentasBean.setCuenta(cuentaBanco);
        boolean esImputable = cuentasBean.validaCuentaMovimiento();
        if (esImputable) {
            MensajesErrores.advertencia("Es necesario Cuenta contable correcta para el banco");
            return true;
        }
        if (!((banco.getFondorotativo() == null) || (banco.getFondorotativo().isEmpty()))) {
            cuentaBanco = cuentasBean.traerCodigo(banco.getFondorotativo());
            cuentasBean.setCuenta(cuentaBanco);
            esImputable = cuentasBean.validaCuentaMovimiento();
            if (esImputable) {
                MensajesErrores.advertencia("Es necesario Cuenta contable correcta para el fondo rotativo");
                return true;
            }
        } else {
            banco.setFondorotativo(null);
        }
        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            // buscar si existe clasiificador ya con esa fuente en ese proyecto
            banco.setFecha(new Date());
//            banco.setCuenta(cuentasBean.getCuenta().getCodigo());
            ejbBancos.create(banco, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(BancosBean.class.getName()).log(Level.SEVERE, null, ex);
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
            banco.setFecha(new Date());
//            banco.setCuenta(cuentasBean.getCuenta().getCodigo());
            ejbBancos.edit(banco, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(BancosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public String borrar() {
        try {
            // buscar que no existan reformas,certificaciones para poder borrar

            ejbBancos.remove(banco, seguridadbean.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(BancosBean.class.getName()).log(Level.SEVERE, null, ex);
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

    public Bancos traer(Integer id) throws ConsultarException {
        return ejbBancos.find(id);
    }

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "BancosVista";
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

    public SelectItem[] getComboBancos() {

        Map parametros = new HashMap();
        parametros.put(";orden", "o.nombre asc");
        parametros.put(";where", "o.fondorotativo is null");
        try {
            return Combos.getSelectItems(ejbBancos.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(BancosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public SelectItem[] getComboBancosf() {

        Map parametros = new HashMap();
        parametros.put(";orden", "o.nombre asc");
        parametros.put(";where", "o.fondorotativo is null");
        try {
            return Combos.getSelectItems(ejbBancos.encontarParametros(parametros), false);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(BancosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public SelectItem[] getComboBancosFondo() {

        Map parametros = new HashMap();
        parametros.put(";orden", "o.nombre asc");
        parametros.put(";where", "o.fondorotativo is not null");
        try {
            return Combos.getSelectItems(ejbBancos.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(BancosBean.class.getName()).log(Level.SEVERE, null, ex);
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

}
