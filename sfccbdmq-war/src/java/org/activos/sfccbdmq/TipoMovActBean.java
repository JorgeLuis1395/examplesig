/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.activos.sfccbdmq;

import org.contabilidad.sfccbdmq.CuentasBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
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
import javax.faces.model.SelectItem;
import org.beans.sfccbdmq.ActivosFacade;
import org.beans.sfccbdmq.TipomovactivosFacade;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Tipomovactivos;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "tipoMovActivosSfccbdmq")
@ViewScoped
public class TipoMovActBean implements Serializable {

    /**
     * Creates a new instance of TipomovactivosBean
     */
    public TipoMovActBean() {
    }
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private Formulario formulario = new Formulario();
    private List<Tipomovactivos> tipos;
    private Tipomovactivos tipo;
    @EJB
    private TipomovactivosFacade ejbTipo;
    @EJB
    private ActivosFacade ejbActivos;
    private Perfiles perfil;

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
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "TiposMovActVista";
        if (perfil == null) {
            MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
        this.setPerfil(parametrosSeguridad.traerPerfil(perfil));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguridad.getGrpUsuario().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, tipo invalido");
//                parametrosSeguridad.cerraSession();
//            }
//        }
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
     * @return the tipos
     */
    public List<Tipomovactivos> getTipos() {
        return tipos;
    }

    /**
     * @param tipos the tipos to set
     */
    public void setTipos(List<Tipomovactivos> tipos) {
        this.tipos = tipos;
    }

    /**
     * @return the
     */
    public Tipomovactivos getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(Tipomovactivos tipo) {
        this.tipo = tipo;
    }

    // colocamos los metodos en verbo
    public String crear() {
        // se deberia chequear perfil?
//        if (!menusBean.getPerfil().getNuevo()) {
//            MensajesErrores.advertencia("No tiene autorización para crear un registro");
//        }
        tipo = new Tipomovactivos();
        tipo.setFecha(new Date());
        cuentasBean.setCuenta(null);
        cuentasBean.setCodigo(null);
        formulario.insertar();
        return null;
    }

    public String modificar() {
//        if (!menusBean.getPerfil().getModificacion()) {
//            MensajesErrores.advertencia("No tiene autorización para modificar un registro");
//        }
        tipo = tipos.get(formulario.getFila().getRowIndex());
        formulario.editar();
        if (tipo.getCuenta() == null) {
            cuentasBean.setCuenta(null);
            cuentasBean.setCodigo(null);
        } else {
            cuentasBean.setCuenta(cuentasBean.traerCodigo(tipo.getCuenta()));
            cuentasBean.setCodigo(tipo.getCuenta());
        }
        return null;
    }

    public String eliminar() {
//        if (!menusBean.getPerfil().getBorrado()) {
//            MensajesErrores.advertencia("No tiene autorización para borrar un registro");
//        }
        tipo = tipos.get(formulario.getFila().getRowIndex());
        if (tipo.getCuenta() == null) {
            cuentasBean.setCuenta(null);
            cuentasBean.setCodigo(null);
        } else {
            cuentasBean.setCuenta(cuentasBean.traerCodigo(tipo.getCuenta()));
            cuentasBean.setCodigo(tipo.getCuenta());
        }
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
//        if (!menusBean.getPerfil().getConsulta()) {
//            MensajesErrores.advertencia("No tiene autorización para consultar");
//            return null;
//        }
        try {
            Map parametros = new HashMap();
            parametros.put(";orden", "o.descripcion");
            tipos = ejbTipo.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(TipoMovActBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
    // acciones de base de datos

    private boolean validar() {

        if ((tipo.getDescripcion() == null) || (tipo.getDescripcion().isEmpty())) {
            MensajesErrores.advertencia("Es necesario Descripción");
            return true;
        }

//        if ((cuentasBean.getCuenta() == null)) {
//            MensajesErrores.advertencia("Es necesario cuenta ");
//            return true;
//        }
//        tipo.setCuenta(cuentasBean.getCuenta().getCodigo());
        if (tipo.getIngreso()) {
            if (tipo.getContabiliza()) {
                // cuenta uno
                if ((tipo.getCuenta1() == null) || (tipo.getCuenta1().isEmpty())) {
                    MensajesErrores.advertencia("Es necesario cuenta ");
                    return true;
                }
                Cuentas cuentaUno = cuentasBean.traerCodigo(tipo.getCuenta1());
                //cuentasBean.setCuenta(cuentaCredito);
                boolean esImputable = cuentaUno.getImputable();
                if (!esImputable) {
                    MensajesErrores.advertencia("No es cuenta Imputable " + cuentaUno.getCodigo());
                    return true;
                }
                //
                if ((tipo.getCuenta() == null) || (tipo.getCuenta().isEmpty())) {
                    MensajesErrores.advertencia("Es necesario cuenta crédito");
                    return true;
                }
                Cuentas cuentaCredito = cuentasBean.traerCodigo(tipo.getCuenta());
                //cuentasBean.setCuenta(cuentaCredito);
                esImputable = cuentaCredito.getImputable();
                if (!esImputable) {
                    MensajesErrores.advertencia("No es cuenta Imputable " + cuentaCredito.getCodigo());
                    return true;
                }
                if ((tipo.getDebito() == null) || (tipo.getDebito().isEmpty())) {
                    MensajesErrores.advertencia("Es necesario cuenta débito");
                    return true;
                }
                Cuentas cuentaDebito = cuentasBean.traerCodigo(tipo.getDebito());
                cuentasBean.setCuenta(cuentaDebito);
                esImputable = cuentaDebito.getImputable();
                if (!esImputable) {
                    MensajesErrores.advertencia("No es cuenta Imputable " + cuentaDebito.getCodigo());
                    return true;
                }
            }
        }
//        if ((tipo.getCuenta() == null) || (tipo.getCuenta().isEmpty())) {
//            MensajesErrores.advertencia("Es necesario cuenta");
//            return true;
//        }
        if (tipo.getIngreso()) {
            if (tipo.getTipo() == null) {
                MensajesErrores.advertencia("Es necesario Tipo de movimiento");
                return true;
            }
        } else {
            // cuenta uno
            if ((tipo.getCuenta1() == null) || (tipo.getCuenta1().isEmpty())) {
                MensajesErrores.advertencia("Es necesario cuenta ");
                return true;
            }
            Cuentas cuentaUno = cuentasBean.traerCodigo(tipo.getCuenta1());
            if (cuentaUno==null){
                MensajesErrores.advertencia("No es cuenta  " + cuentaUno.getCodigo());
                return true;
            }
            //cuentasBean.setCuenta(cuentaCredito);
            boolean esImputable = cuentaUno.getImputable();
            if (!esImputable) {
                MensajesErrores.advertencia("No es cuenta Imputable " + cuentaUno.getCodigo());
                return true;
            }
        }
        return false;
    }

    public String insertar() {

        if (validar()) {
            return null;
        }
        try {
            if (!tipo.getIngreso()) {
                tipo.setTipoasientocontrol(null);
                tipo.setCuenta(null);
                tipo.setDebito(null);
                if (!tipo.getContabiliza()) {
                    tipo.setTipoasiento(null);
                    tipo.setCuenta1(null);
                }
            } else {
                if (!tipo.getContabiliza()) {
                    tipo.setTipoasiento(null);
                    tipo.setCuenta1(null);
                    tipo.setTipoasientocontrol(null);
                    tipo.setCuenta(null);
                    tipo.setDebito(null);
                }
            }
            ejbTipo.create(tipo, parametrosSeguridad.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TipoMovActBean.class.getName()).log(Level.SEVERE, null, ex);
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
            if (!tipo.getIngreso()) {
                tipo.setTipoasientocontrol(null);
                tipo.setCuenta(null);
                tipo.setDebito(null);
                if (!tipo.getContabiliza()) {
                    tipo.setTipoasiento(null);
                    tipo.setCuenta1(null);
                }
            } else {
                if (!tipo.getContabiliza()) {
                    tipo.setTipoasiento(null);
                    tipo.setCuenta1(null);
//                    tipo.setTipoasientocontrol(null);
//                    tipo.setCuenta(null);
//                    tipo.setDebito(null);
                }
            }
            ejbTipo.edit(tipo, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TipoMovActBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borrar() {

        try {
            // ver si existen activos
            Map parametros = new HashMap();
            parametros.put(";where", "o.alta=:tipo");
            parametros.put("tipo", tipo);
            int total = ejbActivos.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("No es posible borrar existen activos en este tipo");
                return null;
            }
            parametros = new HashMap();
            parametros.put(";where", "o.baja=:tipo");
            parametros.put("tipo", tipo);
            total = ejbActivos.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("No es posible borrar existen activos en este tipo");
                return null;
            }
            ejbTipo.remove(tipo, parametrosSeguridad.getLogueado().getUserid());
        } catch (BorrarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TipoMovActBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formulario.cancelar();
        buscar();
        return null;
    }
    // falta el combo

    public Tipomovactivos traer(Integer id) throws ConsultarException {
        return ejbTipo.find(id);
    }

    public SelectItem[] getComboTipoIngresos() {
        try {
//            Map parametros = new HashMap();
            Map parametros = new HashMap();
            parametros.put(";orden", "o.descripcion");
            parametros.put(";where", "o.ingreso=true");
            tipos = ejbTipo.encontarParametros(parametros);
            return Combos.getSelectItems(tipos, false);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TipoMovActBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboTipoIngresosEspacio() {
        try {
            Map parametros = new HashMap();
            parametros.put(";orden", "o.descripcion");
            parametros.put(";where", "o.ingreso=true");
            tipos = ejbTipo.encontarParametros(parametros);
            return Combos.getSelectItems(tipos, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TipoMovActBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboTipoBajas() {
        try {
//            Map parametros = new HashMap();
            Map parametros = new HashMap();
            parametros.put(";orden", "o.descripcion");
            parametros.put(";where", "o.ingreso=false");
            tipos = ejbTipo.encontarParametros(parametros);
            return Combos.getSelectItems(tipos, false);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TipoMovActBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboTipoBajasEspacio() {
        try {
            Map parametros = new HashMap();
            parametros.put(";orden", "o.descripcion");
            parametros.put(";where", "o.ingreso=false");
            tipos = ejbTipo.encontarParametros(parametros);
            return Combos.getSelectItems(tipos, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TipoMovActBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the parametrosSeguridad
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
        int size = 4;
        SelectItem[] items = new SelectItem[size];
        int i = 0;

        items[0] = new SelectItem(null, "--- Seleccione Uno ---");
        items[1] = new SelectItem(0, "Compras");
        items[2] = new SelectItem(1, "Donaciones");
        items[3] = new SelectItem(2, "Otros");
        return items;
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
