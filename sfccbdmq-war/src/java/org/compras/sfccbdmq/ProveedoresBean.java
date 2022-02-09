/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.compras.sfccbdmq;

//import com.icesoft.faces.component.selectinputtext.SelectInputText;

import java.text.DecimalFormat;
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
import org.auxiliares.sfccbdmq.Constantes;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.ProveedoresFacade;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Empresas;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Proveedores;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.EmpresasBean;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.event.TextChangeEvent;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.talento.sfccbdmq.EmpleadosBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "proveedoresSfccbdmq")
@ViewScoped
public class ProveedoresBean extends EmpresasBean {

    /**
     * Creates a new instance of ProveeedoresBean
     */
    public ProveedoresBean() {
        super.tipoen = 0;
    }
    private Perfiles perfil;
    private List<Proveedores> listaProveedores;

    @PostConstruct
    private void activar() {
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilStr = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }

        String nombreForma = "ProveedoresVista";
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            Integer idEmpleado = Integer.parseInt(empleadoString);
            getEmpleadoBean().setEmpleadoSeleccionado(getEmpleadoBean().traer(idEmpleado));
            return;
        } else if (perfilStr == null) {
            MensajesErrores.fatal("Sin perfil válido");
            getParametrosSeguridad().cerraSession();
        }
        this.setPerfil(getSeguridadbean().traerPerfil(perfilStr));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil válido");
            getSeguridadbean().cerraSession();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(getSeguridadbean().getGrpUsuario().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                getSeguridadbean().cerraSession();
//            }
//        }
    }
    private Proveedores proveedor;
//    private Proveedores proveedor = new Proveedores();
    @EJB
    private ProveedoresFacade ejbProveedores;
    private String nombreBanco;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;

    @Override
    public String nuevo() {
        super.nuevo();
        setProveedor(new Proveedores());
        proveedor.setEstado(Boolean.TRUE);
        nombreBanco = null;
        return null;
    }

    @Override
    public String modifica(Empresas empresa) {
        super.modifica(empresa);
        // Traer proveedor

        setProveedor(empresa.getProveedores());
        if (proveedor == null) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.empresa=:empresa");
            parametros.put("empresa", empresa);
            try {
                List<Proveedores> lp = ejbProveedores.encontarParametros(parametros);
                for (Proveedores p : lp) {
                    proveedor = p;
                }
            } catch (ConsultarException ex) {
                Logger.getLogger(ProveedoresBean.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        nombreBanco = null;
        if (proveedor.getBanco() != null) {
            nombreBanco = proveedor.getBanco().getNombre();
        }
        return null;
    }

    @Override
    public String elimina(Empresas empresa) {
        super.elimina(empresa);
        setProveedor(empresa.getProveedores());
        if (proveedor == null) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.empresa=:empresa");
            parametros.put("empresa", empresa);
            try {
                List<Proveedores> lp = ejbProveedores.encontarParametros(parametros);
                proveedor = new Proveedores();
                for (Proveedores p : lp) {
                    proveedor = p;
                }
            } catch (ConsultarException ex) {
                Logger.getLogger(ProveedoresBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        nombreBanco = null;
        if (proveedor.getBanco() != null) {
            nombreBanco = proveedor.getBanco().getNombre();
        }
        return null;
    }

    public boolean validProveedor() {
        // Banco
        if (proveedor.getSpi()==null) {
            proveedor.setSpi(Boolean.FALSE);
        }
        if (proveedor.getSpi()) {
            Codigos banco = codigosBean.traerCodigoNombre(Constantes.BANCOS, nombreBanco);
            proveedor.setBanco(banco);
            if (proveedor.getBanco() == null) {
                MensajesErrores.advertencia("Necesario Banco del proveedor");
                return true;
            }
            if ((proveedor.getRucbeneficiario() == null) || (proveedor.getRucbeneficiario().isEmpty())) {
                MensajesErrores.advertencia("Necesario RUC Beneficiario");
                return true;
            }
            if ((proveedor.getTipocta() == null)) {
                MensajesErrores.advertencia("Necesario Tipo de cuenta de transferencia a Beneficiario");
                return true;
            }
            if ((proveedor.getNombrebeneficiario() == null) || (proveedor.getNombrebeneficiario().isEmpty())) {
                MensajesErrores.advertencia("Necesario Beneficiario");
                return true;
            }
        }
//        if ((proveedor.getDireccion() == null) || (proveedor.getDireccion().isEmpty())) {
//            MensajesErrores.advertencia("Necesario Dirección");
//            return true;
//        }
        if (formulario.isNuevo()) {
            Map parametros = new HashMap();
//            parametros.put(";where", "o.codigo=:codigo");
//            parametros.put("codigo", proveedor.getCodigo());
//            int total;
//            try {
//                total = ejbProveedores.contar(parametros);
//                if (total > 0) {
//                    MensajesErrores.error("Ya existe empresa con ese código");
//                    return true;
//                }
//
//            } catch (ConsultarException ex) {
//                MensajesErrores.fatal(ex.getMessage());
//                Logger.getLogger(EmpresasBean.class.getName()).log(Level.SEVERE, null, ex);
//            }
            if (formulario.isNuevo()) {
                parametros = new HashMap();
                parametros.put(";where", "o.empresa.ruc=:ruc");
                parametros.put("ruc", empresa.getRuc());
                int total = 0;
                try {
                    total = ejbProveedores.contar(parametros);
                    Empresas e = super.taerRuc(ruc);
                    if (total > 0) {
                        MensajesErrores.error("Ya existe empresa con ese ruc");
                        return true;
                    }
                    if (e != null) {
                        empresa = e;
                    }

                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage());
                    Logger.getLogger(EmpresasBean.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }
        return false;
    }
     public String grabarEmpresaProveedor() {
         super.grabarSolo();
         return null;
     }
    @Override
    public String grabar() {
        if (super.validar()) {
            return null;
        }
        if (validProveedor()) {
            return null;
        }
        super.grabar();
        try {
            if (!((nombreBanco == null) || (nombreBanco.isEmpty()))) {
                Codigos bancoProveedor = codigosBean.traerCodigoNombre(Constantes.BANCOS, nombreBanco);
                proveedor.setBanco(bancoProveedor);
            }
            if (proveedor.getId() == null) {
                getProveedor().setEmpresa(super.empresa);
                ejbProveedores.create(proveedor, getSeguridadbean().getLogueado().getUserid());
            } else {
                ejbProveedores.edit(proveedor, getSeguridadbean().getLogueado().getUserid());
            }
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ProveedoresBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        return null;
    }

    @Override
    public String insertar() {
        if (super.validar()) {
            return null;
        }
        if (validProveedor()) {
            return null;
        }
        try {
            if (empresa.getId() == null) {
                super.insertar();
            } else {
                super.grabar();
            }
            if ((nombreBanco == null) || (nombreBanco.isEmpty())) {
                Codigos bancoProveedor = codigosBean.traerCodigoNombre(Constantes.BANCOS, nombreBanco);
                proveedor.setBanco(bancoProveedor);
            }
            if(empresa.getId() == null){
                MensajesErrores.advertencia("El proveedor no esta bien creado");
                return null;
            }
            getProveedor().setEmpresa(empresa);
            String usuario = seguridadbean.getLogueado().getUserid();
            ejbProveedores.create(proveedor, usuario);
            DecimalFormat df = new DecimalFormat("0000");
            proveedor.setCodigo(df.format(proveedor.getId()));
            ejbProveedores.edit(proveedor, usuario);
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ProveedoresBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    /**
     * @return the proveedor
     */
    public Proveedores getProveedor() {
        return proveedor;
    }

    /**
     * @param proveedor the proveedor to set
     */
    public void setProveedor(Proveedores proveedor) {
        this.proveedor = proveedor;
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

    public void sincronizar() {
        if (super.empresa.getProveedores() == null) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.empresa=:empresa");
            parametros.put("empresa", empresa);
            try {
                List<Proveedores> lp = ejbProveedores.encontarParametros(parametros);
                for (Proveedores p : lp) {
                    super.empresa.setProveedores(p);
                }
            } catch (ConsultarException ex) {
                Logger.getLogger(ProveedoresBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public Proveedores traerProveedor(Integer id) throws ConsultarException {
        return ejbProveedores.find(id);
    }

    /**
     * @return the nombreBanco
     */
    public String getNombreBanco() {
        return nombreBanco;
    }

    /**
     * @param nombreBanco the nombreBanco to set
     */
    public void setNombreBanco(String nombreBanco) {
        this.nombreBanco = nombreBanco;
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

    @Override
    public String eliminar() {
        try {
            ejbProveedores.remove(proveedor, seguridadbean.getLogueado().getUserid());
            super.eliminar();
        } catch (BorrarException ex) {
            Logger.getLogger(ProveedoresBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        nuevo();
        formulario.cancelar();
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

    public void proveedorChangeEventHandler(TextChangeEvent event) {
        // Buscar por nombre
        String newWord = event.getNewValue() != null ? (String) event.getNewValue() : "";
        int tbuscar = 0;
        Map parametros = new HashMap();
        String where = "  ";
        empresa = null;
        proveedor = null;
//        where = " o.estado=:estado";
//        parametros.Cput("estado", true);
        //nombre
        where += " upper(o.empresa.nombre) like :nombre";
        parametros.put("nombre", "%"+newWord.toUpperCase() + "%");
        parametros.put(";orden", "o.empresa.nombre");
        try {
            parametros.put(";where", where);
            int total = 15;
//            int total = ((SelectInputText) event.getComponent()).getRows();
            // Contadores
            parametros.put(";inicial", 0);
            parametros.put(";final", total);
            listaProveedores = ejbProveedores.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }

    }

    public void proveedorRucChangeEventHandler(TextChangeEvent event) {
        //Buscra Por RUC
        String newWord = event.getNewValue() != null ? (String) event.getNewValue() : "";
        empresa = null;
        proveedor = null;
        Map parametros = new HashMap();
//        String where = "  o.empresa.estado=:estado ";
        String where = "  ";

//        where = " o.estado=:estado";
//        parametros.put("estado", true);

        //ruc
        where += " o.empresa.ruc like :ruc";
        parametros.put("ruc", newWord + "%");
        parametros.put(";orden", "o.empresa.ruc");
        try {
            parametros.put(";where", where);
            int total = 15;
//            int total = ((SelectInputText) event.getComponent()).getRows();
            // Contadores
            parametros.put(";inicial", 0);
            parametros.put(";final", total);
            listaProveedores = ejbProveedores.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }

    }

    public void proveedorRazonChangeEventHandler(TextChangeEvent event) {
        // nombre comercial  - Razon
        String newWord = event.getNewValue() != null ? (String) event.getNewValue() : "";
        int tbuscar = 0;
        if (getTipobuscar() != null) {
            tbuscar = Integer.parseInt(getTipobuscar());
        }
        Map parametros = new HashMap();
        String where = "   ";
        empresa = null;
        proveedor = null;
//        String where = "  o.empresa.estado=:estado ";

//        where = " o.estado=:estado";
//        parametros.put("estado", true);

        //reazon
        where += " upper(o.empresa.nombrecomercial) like :comercial";
        parametros.put("comercial","%"+ newWord.toUpperCase() + "%");
        parametros.put(";orden", "o.empresa.nombrecomercial");

        try {
            parametros.put(";where", where);
            int total = 15;
//            int total = ((SelectInputText) event.getComponent()).getRows();
            // Contadores
            parametros.put(";inicial", 0);
            parametros.put(";final", total);
            listaProveedores = ejbProveedores.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }

    }

    public void proveedorCodigoChangeEventHandler(TextChangeEvent event) {
        // por codigo
        String newWord = event.getNewValue() != null ? (String) event.getNewValue() : "";
        int tbuscar = 0;

        Map parametros = new HashMap();
//        String where = "  o.empresa.estado=:estado ";
        String where = "  ";

//        where = " o.estado=:estado";
//        parametros.put("estado", true);

        //codigo
        where += " upper(o.codigo) like :codigo";
        parametros.put("codigo", "%"+newWord.toUpperCase() + "%");
        parametros.put(";orden", "o.codigo");

        try {
            parametros.put(";where", where);
            int total = 15;
//            int total = ((SelectInputText) event.getComponent()).getRows();
            // Contadores
            parametros.put(";inicial", 0);
            parametros.put(";final", total);
            listaProveedores = ejbProveedores.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }

    }

    public void cambiaempresa(ValueChangeEvent event) {
        if (listaProveedores == null) {
            return;
        }
        empresa = null;
        proveedor = null;
        String newWord = (String) event.getNewValue();
        for (Proveedores p : listaProveedores) {
            int tbuscar = 0;
            if (getTipobuscar() != null) {
                tbuscar = Integer.parseInt(getTipobuscar());
            }

            switch (Math.abs(tbuscar)) {
                case 1:
                    //ruc
                    if (p.getEmpresa().getRuc().compareToIgnoreCase(newWord) == 0) {
                        proveedor = p;
                        empresa = p.getEmpresa();
                        return;
                    }
                    break;
                case 2:
                    //nombre
                    if (p.getEmpresa().getNombre().compareToIgnoreCase(newWord) == 0) {
                        proveedor = p;
                        empresa = p.getEmpresa();
                        return;
                    }
                    break;
                case 3:
                    //reazon
                    if (p.getEmpresa().getNombrecomercial().compareToIgnoreCase(newWord) == 0) {
                        proveedor = p;
                        empresa = p.getEmpresa();
                        return;
                    }
                    break;
                case 4:
                    //codigo
                    if (p.getCodigo().compareToIgnoreCase(newWord) == 0) {
                        proveedor = p;
                        empresa = p.getEmpresa();
                        return;
                    }
                    break;
                default:
                    break;
            }
        }
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

    /**
     * @return the listaProveedores
     */
    public List<Proveedores> getListaProveedores() {
        return listaProveedores;
    }

    /**
     * @param listaProveedores the listaProveedores to set
     */
    public void setListaProveedores(List<Proveedores> listaProveedores) {
        this.listaProveedores = listaProveedores;
    }
    public String getInformacionBancaria(){
        if (proveedor ==null){
            return "";
        }
        String retorno="<p><strong> Banco:</strong>"+(proveedor.getBanco()==null?"NO EXISTE BANCO":proveedor.getBanco().getNombre())+"</p>";
        retorno+="<p><strong> Cuenta:</strong>"+(proveedor.getCtabancaria()==null?"NO EXISTE CUENTA":proveedor.getCtabancaria())+"</p>";
        retorno+="<p><strong> Tipo:</strong>"+(proveedor.getTipocta()==null?"NO EXISTE TIPO":proveedor.getTipocta().toString())+"</p>";
        return retorno;
    }
}