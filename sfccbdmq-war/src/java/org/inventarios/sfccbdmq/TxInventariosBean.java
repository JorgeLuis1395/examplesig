/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.inventarios.sfccbdmq;

import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.Serializable;
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
import org.beans.sfccbdmq.TxinventariosFacade;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Txinventarios;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "txSuministrosSfccbdmq")
@ViewScoped
public class TxInventariosBean implements Serializable {

    /**
     * Creates a new instance of TxinventariosBean
     */
    public TxInventariosBean() {
    }
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;

    private Formulario formulario = new Formulario();
    private List<Txinventarios> transacciones;
    private Txinventarios transaccion;
    @EJB
    private TxinventariosFacade ejbTransaccion;
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
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            return;
        }
        String nombreForma = "TxSuministrosVista";
//        if (perfil == null) {
//            MensajesErrores.fatal("Sin perfil válido");
//            parametrosSeguridad.cerraSession();
//        }
//        this.setPerfil(parametrosSeguridad.traerPerfil(perfil));
//        if (this.getPerfil() == null) {
//            MensajesErrores.fatal("Sin perfil válido");
//            parametrosSeguridad.cerraSession();
//        }
////        if (nombreForma==null){
////            nombreForma="";
////        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguridad.getGrpUsuario().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
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
     * @return the transacciones
     */
    public List<Txinventarios> getTxinventarios() {
        return transacciones;
    }

    /**
     * @param transacciones the transacciones to set
     */
    public void setTxinventarios(List<Txinventarios> transacciones) {
        this.transacciones = transacciones;
    }

    /**
     * @return the
     */
    public Txinventarios getTransaccion() {
        return transaccion;
    }

    /**
     * @param transaccion the transaccion to set
     */
    public void setTransaccion(Txinventarios transaccion) {
        this.transaccion = transaccion;
    }

    // colocamos los metodos en verbo
    public String crear() {
        transaccion = new Txinventarios();
        formulario.insertar();
        return null;
    }

    public String modificar() {
        transaccion = transacciones.get(formulario.getFila().getRowIndex());
        formulario.editar();
        return null;
    }

    public String eliminar() {
        transaccion = transacciones.get(formulario.getFila().getRowIndex());
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
            parametros.put(";orden", "o.codigo");
            transacciones = ejbTransaccion.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(TxInventariosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
    // acciones de base de datos

    private boolean validar() {
        if ((transaccion.getCodigo() == null) || (transaccion.getCodigo().isEmpty())) {
            MensajesErrores.advertencia("Es necesario código");
            return true;
        }
        if ((transaccion.getIniciales() == null) || (transaccion.getIniciales().isEmpty())) {
            MensajesErrores.advertencia("Es necesario código");
            return true;
        }
        if ((transaccion.getNombre() == null) || (transaccion.getNombre().isEmpty())) {
            MensajesErrores.advertencia("Es necesario nombre");
            return true;
        }
        if ((transaccion.getCuenta() == null) || (transaccion.getCuenta().isEmpty())) {
            MensajesErrores.advertencia("Es necesario cuenta");
            return true;
        }
        if (transaccion.getTipoasiento() == null) {
            MensajesErrores.advertencia("Es necesario tipo de asiento");
            return true;
        }
        // ver si código ya existe
        if (formulario.isNuevo()) {

            Map parametros = new HashMap();
            parametros.put(";where", "o.codigo=:codigo");
            parametros.put("codigo", transaccion.getCodigo());
            try {
                int total = ejbTransaccion.contar(parametros);
                if (total > 0) {
                    MensajesErrores.advertencia("Ya existe transaccion con el código : " + transaccion.getCodigo());
                }
            } catch (ConsultarException ex) {
                Logger.getLogger(TxInventariosBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    public String insertar() {

        if (validar()) {
            return null;
        }
        try {

            ejbTransaccion.create(transaccion, parametrosSeguridad.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TxInventariosBean.class.getName()).log(Level.SEVERE, null, ex);
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
            ejbTransaccion.edit(transaccion, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TxInventariosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borrar() {

        try {
            ejbTransaccion.remove(transaccion, parametrosSeguridad.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TxInventariosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formulario.cancelar();
        buscar();
        return null;
    }
    // falta el combo

    public Txinventarios traer(Integer id) throws ConsultarException {
        return ejbTransaccion.find(id);
    }

    public SelectItem[] getComboTxInventario() {
        try {
//            Map parametros = new HashMap();
            Map parametros = new HashMap();
            parametros.put(";orden", "o.nombre");
            transacciones = ejbTransaccion.encontarParametros(parametros);
            return Combos.getSelectItems(transacciones, false);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TxInventariosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboTxInventarioEspacio() {
        try {
            Map parametros = new HashMap();
            parametros.put(";orden", "o.nombre");
            transacciones = ejbTransaccion.encontarParametros(parametros);
            return Combos.getSelectItems(transacciones, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TxInventariosBean.class.getName()).log(Level.SEVERE, null, ex);
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

    public SelectItem[] getComboIngresos() {
        Map parametros = new HashMap();
        List<Txinventarios> t = new LinkedList<>();
        try {
            parametros.put(";where", " o.ingreso=true");
            parametros.put(";orden", " o.nombre desc");
            t = ejbTransaccion.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.mensaje("ERROR", ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return Combos.getSelectItems(t, true);
    }

    public SelectItem[] getComboEgresos() {
        Map parametros = new HashMap();
        List<Txinventarios> t = new LinkedList<>();
        try {
            parametros.put(";where", " o.ingreso=false");
            parametros.put(";orden", " o.nombre desc");
            t = ejbTransaccion.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.mensaje("ERROR", ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return Combos.getSelectItems(t, true);
    }

    public SelectItem[] getComboEgresosSinproveedor() {
        Map parametros = new HashMap();
        List<Txinventarios> t = new LinkedList<>();
        try {
            parametros.put(";where", " o.ingreso=false and o.proveedor=false  ");
            parametros.put(";orden", " o.nombre desc");
            t = ejbTransaccion.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.mensaje("ERROR", ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return Combos.getSelectItems(t, true);
    }

    public SelectItem[] getComboIngresoProveedor() {
        Map parametros = new HashMap();
        List<Txinventarios> t = new LinkedList<>();
        try {
            parametros.put(";where", " o.ingreso=true and o.proveedor=true  ");
            parametros.put(";orden", " o.nombre desc");
            t = ejbTransaccion.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.mensaje("ERROR", ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return Combos.getSelectItems(t, true);
    }

    public SelectItem[] getComboTipos() {
        Map parametros = new HashMap();
        List<Txinventarios> t = new LinkedList<>();
        try {
//            parametros.put(";where", " o.ingreso=false");
            parametros.put(";orden", " o.nombre desc");
            t = ejbTransaccion.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.mensaje("ERROR", ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return Combos.getSelectItems(t, true);
    }

    public SelectItem[] getComboIngresoEgreso() {
        Map parametros = new HashMap();
        List<Txinventarios> t = new LinkedList<>();
        try {
            parametros.put(";where", " o.transaferencia is null and o.transformacion is null");
            parametros.put(";orden", " o.nombre desc");
            t = ejbTransaccion.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.mensaje("ERROR", ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return Combos.getSelectItems(t, true);
    }

    public SelectItem[] getComboTransaferencia() {
        Map parametros = new HashMap();
        List<Txinventarios> t = new LinkedList<>();
        try {
            parametros.put(";where", " o.ingreso=false and o.transaferencia is not null ");
            parametros.put(";orden", " o.nombre desc");
            t = ejbTransaccion.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.mensaje("ERROR", ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return Combos.getSelectItems(t, true);
    }

    public SelectItem[] getComboTransformacion() {
        Map parametros = new HashMap();
        List<Txinventarios> t = new LinkedList<>();
        try {
            parametros.put(";where", " o.ingreso=false  and o.transformacion is not null");
            //parametros.put(";where", " o.ingreso=true and o.transaferencia is null and o.transformacion is not null");
            parametros.put(";orden", " o.nombre desc");
            t = ejbTransaccion.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.mensaje("ERROR", ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return Combos.getSelectItems(t, true);
    }
}
