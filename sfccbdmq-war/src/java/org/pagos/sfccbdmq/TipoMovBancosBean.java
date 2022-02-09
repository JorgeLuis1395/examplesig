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
import org.beans.sfccbdmq.TipomovbancosFacade;
import org.contabilidad.sfccbdmq.CuentasBean;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Tipomovbancos;
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
@ManagedBean(name = "tipoMovBancosSfccbdmq")
@ViewScoped
public class TipoMovBancosBean {

    /**
     * Creates a new instance of TipomovbancosBean
     */
    public TipoMovBancosBean() {
    }
    private Tipomovbancos tipomov;
    private List<Tipomovbancos> tipomovbancos;
    private Formulario formulario = new Formulario();
    private Perfiles perfil;
    @EJB
    private TipomovbancosFacade ejbTipomovbancos;
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
     * @return the tipomov
     */
    public Tipomovbancos getTipomov() {
        return tipomov;
    }

    /**
     * @param tipomov the tipomov to set
     */
    public void setTipomov(Tipomovbancos tipomov) {
        this.tipomov = tipomov;
    }

    /**
     * @return the tipomovbancos
     */
    public List<Tipomovbancos> getTipomovbancos() {
        return tipomovbancos;
    }

    /**
     * @param tipomovbancos the tipomovbancos to set
     */
    public void setTipomovbancos(List<Tipomovbancos> tipomovbancos) {
        this.tipomovbancos = tipomovbancos;
    }

    public String buscar() {

        Map parametros = new HashMap();
        parametros.put(";orden", "o.descripcion asc");
        try {
            tipomovbancos = ejbTipomovbancos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(TipoMovBancosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String crear() {

        formulario.insertar();
        tipomov = new Tipomovbancos();
        cuentasBean.setCuenta(null);
        cuentasBean.setCodigo(null);
        return null;
    }

    public String modificar(Tipomovbancos tipomov) {

        this.tipomov = tipomov;
        formulario.editar();
        if (tipomov.getCuenta() == null) {
            cuentasBean.setCuenta(null);
            cuentasBean.setCodigo(null);
        } else {
            cuentasBean.setCuenta(cuentasBean.traerCodigo(tipomov.getCuenta()));
            cuentasBean.setCodigo(tipomov.getCuenta());
        }
        return null;
    }

    public String eliminar(Tipomovbancos tipomov) {

        this.tipomov = tipomov;
        formulario.eliminar();
        if (tipomov.getCuenta() == null) {
            cuentasBean.setCuenta(null);
            cuentasBean.setCodigo(null);
        } else {
            cuentasBean.setCuenta(cuentasBean.traerCodigo(tipomov.getCuenta()));
            cuentasBean.setCodigo(tipomov.getCuenta());
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

        if ((tipomov.getDescripcion() == null) || (tipomov.getDescripcion().isEmpty())) {
            MensajesErrores.advertencia("Es necesario Descripción");
            return true;
        }
        if (getPideCuenta()) {
            if (cuentasBean.getCuenta() == null) {
                MensajesErrores.advertencia("Es necesario Cuenta contable");
                return true;
            }
            tipomov.setCuenta(cuentasBean.getCuenta().getCodigo());
            return cuentasBean.validaCuentaMovimiento();
        } else {
            tipomov.setCuenta(null);
        }
        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            // buscar si existe clasiificador ya con esa fuente en ese proyecto
            tipomov.setFecha(new Date());

            ejbTipomovbancos.create(tipomov, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(TipoMovBancosBean.class.getName()).log(Level.SEVERE, null, ex);
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
            tipomov.setFecha(new Date());
//            tipomov.setCuenta(cuentasBean.getCuenta().getCodigo());
            ejbTipomovbancos.edit(tipomov, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(TipoMovBancosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public String borrar() {
        try {
            // buscar que no existan reformas,certificaciones para poder borrar

            ejbTipomovbancos.remove(tipomov, seguridadbean.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(TipoMovBancosBean.class.getName()).log(Level.SEVERE, null, ex);
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

    public Tipomovbancos traer(Integer id) throws ConsultarException {
        return ejbTipomovbancos.find(id);
    }

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "TipomovbancosVista";
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

    public SelectItem[] getComboAnticiposEmpleados() {

        Map parametros = new HashMap();
        parametros.put(";orden", "o.descripcion asc");
        parametros.put(";where", "o.tipo='ANTE'");
        try {
            return Combos.getSelectItems(ejbTipomovbancos.encontarParametros(parametros), false);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(TipoMovBancosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public SelectItem[] getComboAnticiposProveedores() {

        Map parametros = new HashMap();
        parametros.put(";orden", "o.descripcion asc");
        parametros.put(";where", "o.tipo='ANTP'");
        try {
            return Combos.getSelectItems(ejbTipomovbancos.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(TipoMovBancosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public SelectItem[] getComboIngresosBancos() {

        Map parametros = new HashMap();
        parametros.put(";orden", "o.descripcion asc");
        parametros.put(";where", "o.tipo='ING' and o.ingreso=true");
        try {
            return Combos.getSelectItems(ejbTipomovbancos.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(TipoMovBancosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboObligacionesBancos() {

        Map parametros = new HashMap();
        parametros.put(";orden", "o.descripcion asc");
        parametros.put(";where", "o.tipo='OBL'");
        try {
            return Combos.getSelectItems(ejbTipomovbancos.encontarParametros(parametros), false);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(TipoMovBancosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboOtrosBancos() {

        Map parametros = new HashMap();
        parametros.put(";orden", "o.descripcion asc");
        parametros.put(";where", "o.tipo in ('OTR','CAJA','VIAT')");
        try {
            return Combos.getSelectItems(ejbTipomovbancos.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(TipoMovBancosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboReboteIngresoBancos() {

        Map parametros = new HashMap();
        parametros.put(";orden", "o.descripcion asc");
        parametros.put(";where", "o.tipo='REB' and o.ingreso=true");
        try {
            return Combos.getSelectItems(ejbTipomovbancos.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(TipoMovBancosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboReboteEgresoBancos() {

        Map parametros = new HashMap();
        parametros.put(";orden", "o.descripcion asc");
        parametros.put(";where", "o.tipo='REB' and o.ingreso=false");
        try {
            return Combos.getSelectItems(ejbTipomovbancos.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(TipoMovBancosBean.class.getName()).log(Level.SEVERE, null, ex);
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

    public SelectItem[] getComboTipo() {
        int size = 10;
        SelectItem[] items = new SelectItem[size];
        int i = 0;

        items[0] = new SelectItem(null, "--- Seleccione Uno ---");
        items[1] = new SelectItem("ANTP", "Anticipos Proveedores");
        items[2] = new SelectItem("ANTE", "Anticipos Empleados");
        items[3] = new SelectItem("OBL", "Obligaciones");
        items[4] = new SelectItem("ING", "Ingresos ");
        items[5] = new SelectItem("OTR", "Otros");
        items[6] = new SelectItem("CAJA", "Caja Chica");
        items[7] = new SelectItem("VIAT", "Viáticos");
        items[8] = new SelectItem("REB", "Rebotes");
        items[9] = new SelectItem("FOND", "Fondos a Rendir");
        return items;
    }

    public boolean getPideCuenta() {
        if (tipomov == null) {
            return false;
        }
        if (tipomov.getTipo() == null) {
            return false;
        }
        if (tipomov.getTipo().equals("OTR")) {
            return true;
        }
        if (tipomov.getTipo().equals("CAJA")) {
            return true;
        }
        if (tipomov.getTipo().equals("VIAT")) {
            return true;
        }
        if (tipomov.getTipo().equals("REB")) {
            return true;
        }
        return false;
    }
}