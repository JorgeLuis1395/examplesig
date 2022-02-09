/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pagos.sfccbdmq;

//import com.icesoft.faces.component.selectinputtext.SelectInputText;
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
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.ClientesFacade;
import org.beans.sfccbdmq.CuentasFacade;
import org.entidades.sfccbdmq.Clientes;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Empresas;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.EmpresasBean;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.event.TextChangeEvent;
import org.seguridad.sfccbdmq.CodigosBean;
import org.talento.sfccbdmq.EmpleadosBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "clientesSfccbdmq")
@ViewScoped
public class ClientesBean extends EmpresasBean {

    /**
     * Creates a new instance of ProveeedoresBean
     */
    public ClientesBean() {
        super.tipoen = 2;
        setTipobuscar("2");
    }
    private Perfiles perfil;

    @PostConstruct
    private void activar() {
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilStr = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ClientesVista";
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
    private Clientes cliente = new Clientes();
    private String nombreBanco;
    private List<Clientes> listaClientes;
    private Cuentas cuenta;

//    private String nombre;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;

    @EJB
    private ClientesFacade ejbClientes;
    @EJB
    private CuentasFacade ejbCuentas;

    @Override
    public String nuevo() {
        super.nuevo();
        setCliente(new Clientes());
        nombreBanco = null;
        return null;
    }

    @Override
    public String modifica(Empresas empresa) {
        super.modifica(empresa);

        try {
            // Traer cliente
            setCliente(empresa.getClientes());
            if (cliente == null) {
                Map parametros = new HashMap();
                parametros.put(";where", "o.empresa=:empresa");
                parametros.put("empresa", empresa);
                cliente = new Clientes();
                List<Clientes> lp = ejbClientes.encontarParametros(parametros);
                for (Clientes p : lp) {
                    cliente = p;
                }
            }
            //Traer cuenta
            if (cliente.getCuentaingresos() != null) {
                Map parametros = new HashMap();
                parametros.put(";where", "o.codigo=:codigo");
                parametros.put("codigo", cliente.getCuentaingresos());
                List<Cuentas> lista = ejbCuentas.encontarParametros(parametros);
                if (!lista.isEmpty()) {
                    cuenta = lista.get(0);
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ClientesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        nombreBanco = null;

        return null;
    }

    @Override
    public String elimina(Empresas empresa) {
        super.elimina(empresa);
        setCliente(empresa.getClientes());
        if (cliente == null) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.empresa=:empresa");
            parametros.put("empresa", empresa);
            try {
                List<Clientes> lp = ejbClientes.encontarParametros(parametros);
                for (Clientes p : lp) {
                    cliente = p;
                }
            } catch (ConsultarException ex) {
                Logger.getLogger(ClientesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return null;
    }

    public boolean validCliente() {
        // Banco
        if (formulario.isNuevo()) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.empresa.ruc=:ruc");
            parametros.put("ruc", empresa.getRuc());
            int total = 0;
            try {
                total = ejbClientes.contar(parametros);
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
        if (cuenta == null) {
            MensajesErrores.advertencia("Seleccione un tipo de cuenta");
            return true;
        }
        return false;
    }

    @Override
    public String grabar() {
        if (super.validar()) {
            return null;
        }
        if (validCliente()) {
            return null;
        }
        empresa.setDireccion(cliente.getDireccion());
        super.grabar();
        try {
            getCliente().setCuentaingresos(cuenta.getCodigo());
            if (cliente.getId() == null) {
                getCliente().setEmpresa(super.empresa);
                ejbClientes.create(cliente, getSeguridadbean().getLogueado().getUserid());
            } else {
                ejbClientes.edit(cliente, getSeguridadbean().getLogueado().getUserid());
            }
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ClientesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        return null;
    }

    @Override
    public String insertar() {
        if (super.validar()) {
            return null;
        }
        if (validCliente()) {
            return null;
        }
        try {
            empresa.setDireccion(cliente.getDireccion());
            if (empresa.getId() == null) {
                super.insertar();
            } else {
                super.grabar();
            }
            getCliente().setEmpresa(empresa);
            getCliente().setCuentaingresos(cuenta.getCodigo());
            String usuario = seguridadbean.getLogueado().getUserid();
            ejbClientes.create(cliente, usuario);

        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ClientesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    /**
     * @return the cliente
     */
    public Clientes getCliente() {
        return cliente;
    }

    /**
     * @param cliente the cliente to set
     */
    public void setCliente(Clientes cliente) {
        this.cliente = cliente;
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
        if (super.empresa.getClientes() == null) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.empresa=:empresa");
            parametros.put("empresa", empresa);
            try {
                List<Clientes> lp = ejbClientes.encontarParametros(parametros);
                for (Clientes p : lp) {
                    super.empresa.setClientes(p);
                }
            } catch (ConsultarException ex) {
                Logger.getLogger(ClientesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public Clientes traerCliente(Integer id) throws ConsultarException {
        return ejbClientes.find(id);
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
            ejbClientes.remove(cliente, seguridadbean.getLogueado().getUserid());
            super.eliminar();
        } catch (BorrarException ex) {
            Logger.getLogger(ClientesBean.class.getName()).log(Level.SEVERE, null, ex);
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

//    @Override
//    public void cambiaempresa(ValueChangeEvent event) {
//        empresa = null;
//        cliente = null;
////        empresa = null;
//        if (event.getComponent() instanceof SelectInputText) {
//            // get the number of displayable records from the component
//            SelectInputText autoComplete
//                    = (SelectInputText) event.getComponent();
//            // get the new value typed by component user.
//            String newWord = (String) event.getNewValue();
//            //        getEmpresasBeans().setComercial("");
//            if ((newWord == null) || (newWord.isEmpty())) {
//                empresa = null;
//                cliente = null;
//                return;
//            }
//            List<Clientes> aux = new LinkedList<>();
//            // traer la consulta
//            Map parametros = new HashMap();
//            String where = "  o.empresa.estado=:estado ";
//            int tbuscar = 0;
//            if (getTipobuscar() != null) {
//                tbuscar = Integer.parseInt(getTipobuscar());
//            }
////            if (tbuscar > 0) {//cleinte
////                where = " o.clientes is  not null and o.estado=:estado";
////            } else if (tbuscar < 0) {//cliente
////                where = " o.clientes is not null and o.estado=:estado";
////            }
//            parametros.put("estado", true);
//            if (Math.abs(tbuscar) == 1) {//ruc
//                where += " and o.empresa.ruc like :ruc";
//                parametros.put("ruc", newWord + "%");
//                parametros.put(";orden", "o.empresa.ruc");
//            } else if (Math.abs(tbuscar) == 2) {//nombre
//                where += " and upper(o.empresa.nombre) like :nombre";
//                parametros.put("nombre", newWord.toUpperCase() + "%");
//                parametros.put(";orden", "o.emsnombre");
//            } else if (Math.abs(tbuscar) == 3) {//reazon
//                where += " and upper(o.empresa.nombrecomercial) like :comercial";
//                parametros.put("comercial", newWord.toUpperCase() + "%");
//                parametros.put(";orden", "o.empresa.nombrecomercial");
//            } else if (Math.abs(tbuscar) == 4) {//reazon
//                where += " and upper(o.codigo) like :codigo";
//                parametros.put("codigo", newWord.toUpperCase() + "%");
//                parametros.put(";orden", "o.codigo");
//            }
//
//            parametros.put(";where", where);
//
//            int total = ((SelectInputText) event.getComponent()).getRows();
//            // Contadores
//
//            parametros.put(";inicial", 0);
//            parametros.put(";final", total);
//            try {
//                aux = ejbClientes.encontarParametros(parametros);
//            } catch (ConsultarException ex) {
//                Logger.getLogger(EmpresasBean.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            setLbuscar(new ArrayList());
//            for (Clientes e : aux) {
//                String texto = "";
//                if (Math.abs(tbuscar) == 1) {//ruc
//                    texto = e.getEmpresa().getRuc();
//                } else if (Math.abs(tbuscar) == 2) {//nombre
//                    texto = e.getEmpresa().getNombre();
//                } else if (Math.abs(tbuscar) == 3) {//reazon
//                    texto = e.getEmpresa().getNombrecomercial();
//                }
//
//                SelectItem s = new SelectItem(e, texto);
//                getLbuscar().add(s);
//            }
//            if (autoComplete.getSelectedItem() != null) {
//                setCliente((Clientes) autoComplete.getSelectedItem().getValue());
//                empresa = cliente.getEmpresa();
//            } else {
//
//                Clientes tmp = null;
//                for (int i = 0, max = getLbuscar().size(); i < max; i++) {
//                    SelectItem e = (SelectItem) getLbuscar().get(i);
//                    if (e.getLabel().compareToIgnoreCase(newWord) == 0) {
//                        tmp = (Clientes) e.getValue();
//                    }
//
//                }
//                if (tmp != null) {
//                    setCliente(tmp);
//                    empresa = cliente.getEmpresa();
//                }
//            }
//
//        }
//    }
//    /**
//     * @return the nombre
//     */
//    public String getNombre() {
//        return nombre;
//    }
//
//    /**
//     * @param nombre the nombre to set
//     */
//    public void setNombre(String nombre) {
//        this.nombre = nombre;
//    }
    public void cambiaempresa(ValueChangeEvent event) {
        if (listaClientes == null) {
            return;
        }
        empresa = null;
        cliente = null;
        String newWord = (String) event.getNewValue();
        for (Clientes p : listaClientes) {
            int tbuscar = 0;
            if (getTipobuscar() != null) {
                tbuscar = Integer.parseInt(getTipobuscar());
            }

            switch (Math.abs(tbuscar)) {
                case 1:
                    //ruc
                    if (p.getEmpresa().getRuc().compareToIgnoreCase(newWord) == 0) {
                        cliente = p;
                        empresa = p.getEmpresa();
                        return;
                    }
                    break;
                case 2:
                    //nombre
                    if (p.getEmpresa().getNombre().compareToIgnoreCase(newWord) == 0) {
                        cliente = p;
                        empresa = p.getEmpresa();
                        return;
                    }
                    break;
                case 3:
                    //reazon
                    if (p.getEmpresa().getNombrecomercial().compareToIgnoreCase(newWord) == 0) {
                        cliente = p;
                        empresa = p.getEmpresa();
                        return;
                    }
                    break;
//                case 4:
//                    //codigo
//                    if (p.getCodigo().compareToIgnoreCase(newWord) == 0) {
//                        cliente = p;
//                        empresa = p.getEmpresa();
//                        return;
//                    }   break;
                default:
                    break;
            }
        }
    }

    public void proveedorChangeEventHandler(TextChangeEvent event) {

        String newWord = event.getNewValue() != null ? (String) event.getNewValue() : "";
        int tbuscar = 0;
        if (getTipobuscar() != null) {
            tbuscar = Integer.parseInt(getTipobuscar());
        }
        Map parametros = new HashMap();
        String where = "  o.empresa.estado=:estado ";

//        where = " o.estado=:estado";
        parametros.put("estado", true);
        if (Math.abs(tbuscar) == 1) {//ruc  
            where += " and o.empresa.ruc like :ruc";
            parametros.put("ruc", newWord + "%");
            parametros.put(";orden", "o.empresa.ruc");
        } else if (Math.abs(tbuscar) == 2) {//nombre
            where += " and upper(o.empresa.nombre) like :nombre";
            parametros.put("nombre", newWord.toUpperCase() + "%");
            parametros.put(";orden", "o.empresa.nombre");
        } else if (Math.abs(tbuscar) == 3) {//reazon
            where += " and upper(o.empresa.nombrecomercial) like :comercial";
            parametros.put("comercial", newWord.toUpperCase() + "%");
            parametros.put(";orden", "o.empresa.nombrecomercial");
        } else if (Math.abs(tbuscar) == 4) {//reazon
            where += " and upper(o.codigo) like :codigo";
            parametros.put("codigo", newWord.toUpperCase() + "%");
            parametros.put(";orden", "o.codigo");
        }
        try {
            parametros.put(";where", where);
            int total = 15;
//            int total = ((SelectInputText) event.getComponent()).getRows();
            // Contadores
            parametros.put(";inicial", 0);
            parametros.put(";final", total);
            listaClientes = ejbClientes.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }

    }

    public SelectItem[] getComboCuentas113() {

        Map parametros = new HashMap();
        parametros.put(";orden", "o.codigo");
        parametros.put(";where", "o.codigo like '113%' and o.imputable=true");
        try {
            return Combos.getSelectItems(ejbCuentas.encontarParametros(parametros), false);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(TipoMovBancosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the listaClientes
     */
    public List<Clientes> getListaClientes() {
        return listaClientes;
    }

    /**
     * @param listaClientes the listaClientes to set
     */
    public void setListaClientes(List<Clientes> listaClientes) {
        this.listaClientes = listaClientes;
    }

    /**
     * @return the cuenta
     */
    public Cuentas getCuenta() {
        return cuenta;
    }

    /**
     * @param cuenta the cuenta to set
     */
    public void setCuenta(Cuentas cuenta) {
        this.cuenta = cuenta;
    }
}
