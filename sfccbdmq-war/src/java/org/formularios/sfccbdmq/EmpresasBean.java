/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.formularios.sfccbdmq;

//import com.icesoft.faces.component.selectinputtext.SelectInputText;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.ContactosFacade;
import org.beans.sfccbdmq.DireccionesFacade;
import org.beans.sfccbdmq.EmpresasFacade;
import org.beans.sfccbdmq.ProveedoresFacade;
import org.beans.sfccbdmq.TelefonosFacade;
import org.compras.sfccbdmq.ProveedoresBean;
import org.entidades.sfccbdmq.Contactos;
import org.entidades.sfccbdmq.Direcciones;
import org.entidades.sfccbdmq.Empresas;
import org.entidades.sfccbdmq.Proveedores;
import org.entidades.sfccbdmq.Telefonos;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "empresasSfccbdmq")
@SessionScoped
public abstract class EmpresasBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of EmpresasBean
     */
    public EmpresasBean() {
        empresas = new LazyDataModel<Empresas>() {

            @Override
            public List<Empresas> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }
    protected String ruc;
//    private String nombre;
    protected String razon;
    protected String nombre;
    protected Formulario formulario = new Formulario();
    protected Empresas empresa;
    protected int tipoen; //0 proveedor 1 Cliente
    private LazyDataModel<Empresas> empresas;
    //para seleccion
    protected List lbuscar;
    // para direcciones
    private List<Direcciones> direcciones;
    private Direcciones direccion;
    private Formulario formularioDirecciones = new Formulario();
    private Telefonos telefono1;
    private Telefonos telefono2;
    private Telefonos telefono3;
    private String tipobuscar = "-2";
    // para contactos
    private List<Contactos> contactos;
    private Contactos contacto;
    private Formulario formularioContactos = new Formulario();
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    protected ParametrosSfccbdmqBean seguridadbean;
    @EJB
    protected EmpresasFacade ejbEmpresa;
    @EJB
    private ContactosFacade ejbContactos;
    @EJB
    private DireccionesFacade ejbDirecciones;
    @EJB
    private TelefonosFacade ejbTelefonos;
    @EJB
    private ProveedoresFacade ejbProveedores;
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
     * @return the ruc
     */
    public String getRuc() {
        return ruc;
    }

    /**
     * @param ruc the ruc to set
     */
    public void setRuc(String ruc) {
        this.ruc = ruc;
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

    /**
     * @return the razon
     */
    public String getRazon() {
        return razon;
    }

    /**
     * @param razon the razon to set
     */
    public void setRazon(String razon) {
        this.razon = razon;
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
     * @return the empresa
     */
    public Empresas getEmpresa() {
        return empresa;
    }

    /**
     * @param empresa the empresa to set
     */
    public void setEmpresa(Empresas empresa) {
        this.empresa = empresa;

    }

    /**
     * @return the lbuscar
     */
    public List getLbuscar() {
        return lbuscar;
    }

    /**
     * @param lbuscar the lbuscar to set
     */
    public void setLbuscar(List lbuscar) {
        this.lbuscar = lbuscar;
    }

    /**
     * @return the direcciones
     */
    public List<Direcciones> getDirecciones() {
        return direcciones;
    }

    /**
     * @param direcciones the direcciones to set
     */
    public void setDirecciones(List<Direcciones> direcciones) {
        this.direcciones = direcciones;
    }

    /**
     * @return the direccion
     */
    public Direcciones getDireccion() {
        return direccion;
    }

    /**
     * @param direccion the direccion to set
     */
    public void setDireccion(Direcciones direccion) {
        this.direccion = direccion;
    }

    /**
     * @return the contactos
     */
    public List<Contactos> getContactos() {
        return contactos;
    }

    /**
     * @param contactos the contactos to set
     */
    public void setContactos(List<Contactos> contactos) {
        this.contactos = contactos;
    }

    /**
     * @return the contacto
     */
    public Contactos getContacto() {
        return contacto;
    }

    /**
     * @param contacto the contacto to set
     */
    public void setContacto(Contactos contacto) {
        this.contacto = contacto;
    }

    /**
     * @return the formularioDirecciones
     */
    public Formulario getFormularioDirecciones() {
        return formularioDirecciones;
    }

    /**
     * @param formularioDirecciones the formularioDirecciones to set
     */
    public void setFormularioDirecciones(Formulario formularioDirecciones) {
        this.formularioDirecciones = formularioDirecciones;
    }

    /**
     * @return the formularioContactos
     */
    public Formulario getFormularioContactos() {
        return formularioContactos;
    }

    /**
     * @param formularioContactos the formularioContactos to set
     */
    public void setFormularioContactos(Formulario formularioContactos) {
        this.formularioContactos = formularioContactos;
    }

    protected boolean validar() {
        if ((empresa.getNombrecomercial() == null) || (empresa.getNombrecomercial().isEmpty())) {
            MensajesErrores.advertencia("Necesario Nombre comercial");
            return true;
        }
        if ((empresa.getNombre() == null) || (empresa.getNombre().isEmpty())) {
            MensajesErrores.advertencia("Necesario Nombre");
            return true;
        }
        if ((empresa.getRuc() == null) || (empresa.getRuc().isEmpty())) {
            MensajesErrores.advertencia("Necesario RUC");
            return true;
        }
        if ((empresa.getEmail() == null) || (empresa.getEmail().isEmpty())) {
            MensajesErrores.advertencia("Necesario email");
            return true;
        }

        return false;
    }

    //
    public Empresas traer(Integer id) throws ConsultarException {
        return ejbEmpresa.find(id);
    }

    // Para Direcciones
    public String buscarDireccion() {
        if (getEmpresa() == null) {
            MensajesErrores.informacion("Seleccione una empresa");
            return null;
        }
        String where = " o.empresa=:empresa";
        Map parametros = new HashMap();
        parametros.put(";where", where);
        parametros.put("empresa", getEmpresa());
        try {
            direcciones = ejbDirecciones.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger(EmpresasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    // activa formulario

    public SelectItem[] getEstablecimientos() {
        buscarDireccion();
        return Combos.getSelectItems(direcciones, false);
    }

    public Direcciones traerDirecion(Integer id) throws ConsultarException {
        return ejbDirecciones.find(id);
    }

    public String crearDireccion() {
        if (getEmpresa() == null) {
            MensajesErrores.informacion("Seleccione una empresa");
            return null;
        }
        formularioDirecciones.insertar();
        direccion = new Direcciones();
        direccion.setEmpresa(empresa);
        return null;
    }

    public String editarDireccion() {

        formularioDirecciones.editar();
        direccion = direcciones.get(formularioDirecciones.getFila().getRowIndex());
        formularioDirecciones.setIndiceFila(formularioDirecciones.getFila().getRowIndex());
        return null;
    }

    public String borrarDireccion() {
        formularioDirecciones.eliminar();
        direccion = direcciones.get(formularioDirecciones.getFila().getRowIndex());
        formularioDirecciones.setIndiceFila(formularioDirecciones.getFila().getRowIndex());
        return null;
    }
    // valida accion

    public boolean validarDirecciones() {
        if ((direccion.getPrincipal() == null) || (direccion.getPrincipal().isEmpty())) {
            MensajesErrores.advertencia("Principal Obligatorio");
            return true;
        }
//        if ((direccion.getEstablecimiento() == null) || (direccion.getEstablecimiento().isEmpty())) {
//            MensajesErrores.advertencia("Código Obligatorio");
//            return true;
//        }
        if ((direccion.getSecundaria() == null) || (direccion.getSecundaria().isEmpty())) {
            MensajesErrores.advertencia("Secundaria Obligatorio");
            return true;
        }
        if ((direccion.getNumero() == null) || (direccion.getNumero().isEmpty())) {
            MensajesErrores.advertencia("Número Obligatorio");
            return true;
        }
//        if ((direccion.getDescripcion() == null) || (direccion.getDescripcion().isEmpty())) {
//            MensajesErrores.advertencia("Observaciones Obligatorio");
//            return true;
//        }
        return false;
    }
    //ejecuta accion

    public String insertarDireccion() {
        if (validarDirecciones()) {
            return null;
        }
        try {
            ejbDirecciones.create(direccion, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger(EmpresasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        cancelarDireccion();
        return null;
    }

    public String grabarDireccion() {
        if (validarDirecciones()) {
            return null;
        }
        try {
            ejbDirecciones.edit(direccion, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger(EmpresasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        cancelarDireccion();
        return null;
    }

    public String eliminarDireccion() {
        try {
            ejbDirecciones.remove(direccion, seguridadbean.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger(EmpresasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        cancelarDireccion();
        return null;
    }

    public String cancelarDireccion() {
        formularioDirecciones.cancelar();
        buscarDireccion();
        return null;
    }

    // Para Contactos
    public String buscarContacto() {
        if (getEmpresa() == null) {
            MensajesErrores.informacion("Seleccione una empresa primero");
            return null;
        }
        String where = " o.empresa=:empresa";
        Map parametros = new HashMap();
        parametros.put(";where", where);
        parametros.put("empresa", getEmpresa());
        try {
            contactos = ejbContactos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger(EmpresasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    // activa formulario

    public String crearContacto() {
        if (getEmpresa() == null) {
            MensajesErrores.informacion("Seleccione una empresa primero");
            return null;
        }
        getFormularioContactos().insertar();
        contacto = new Contactos();
        contacto.setEmpresa(empresa);
        return null;
    }

    public String editarContacto() {

        getFormularioContactos().editar();
        contacto = contactos.get(formularioContactos.getFila().getRowIndex());
        return null;
    }

    public String borrarContacto() {
        getFormularioContactos().eliminar();
        contacto = contactos.get(formularioContactos.getFila().getRowIndex());
        return null;
    }
    // valida accion

    public boolean validarContacto() {
        if ((contacto.getApellido() == null) || (contacto.getApellido().isEmpty())) {
            MensajesErrores.advertencia("Apellido Obligatorio");
            return true;
        }
        if ((contacto.getNombre() == null) || (contacto.getNombre().isEmpty())) {
            MensajesErrores.advertencia("Nombre Obligatorio");
            return true;
        }
        if ((contacto.getEmail() == null) || (contacto.getEmail().isEmpty())) {
            MensajesErrores.advertencia("e-mail Obligatorio");
            return true;
        }
        if ((contacto.getTelefono() == null) || (contacto.getTelefono().isEmpty())) {
            MensajesErrores.advertencia("Teléfono Obligatorio");
            return true;
        }
        if ((contacto.getFechan() == null)) {
            MensajesErrores.advertencia("Fecha Nacimiento Obligatorio");
            return true;
        }
        if ((contacto.getTitulo() == null)) {
            MensajesErrores.advertencia("Títulos Obligatorio");
            return true;
        }
        if ((contacto.getCargo() == null)) {
            MensajesErrores.advertencia("Cargo Obligatorio");
            return true;
        }
        return false;
    }
    //ejecuta accion

    public String insertarContacto() {
        if (validarContacto()) {
            return null;
        }
        try {
            ejbContactos.create(contacto, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger(EmpresasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        cancelarContacto();
        return null;
    }

    public String grabarContacto() {
        if (validarContacto()) {
            return null;
        }
        try {
            ejbContactos.edit(contacto, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger(EmpresasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        cancelarContacto();
        return null;
    }

    public String eliminarContacto() {
        try {
            ejbContactos.remove(contacto, seguridadbean.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger(EmpresasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        cancelarContacto();
        return null;
    }

    public String cancelarContacto() {
        formularioContactos.cancelar();
        buscarContacto();
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

    /**
     * @return the empresas
     */
    public LazyDataModel<Empresas> getEmpresas() {
        return empresas;
    }

    /**
     * @param empresas the empresas to set
     */
    public void setEmpresas(LazyDataModel<Empresas> empresas) {
        this.empresas = empresas;
    }

    public String buscar() {
        empresas = new LazyDataModel<Empresas>() {

            @Override
            public List<Empresas> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                if (scs.length == 0) {
                    parametros.put(";orden", "o.nombre");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }
                String where = "  o.estado=true";
                if (tipoen == 0) {
                    where += " and o.proveedores is not null";
                } else {
                    where += " and o.clientes is not null";
                }
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }
                if (!((nombre == null) || (nombre.isEmpty()))) {
                    where += " and upper(o.nombre) like :nombre";
                    parametros.put("nombre", getNombre().toUpperCase() + "%");
                }
                if (!((razon == null) || (razon.isEmpty()))) {
                    where += " and upper(o.nombrecomercial) like :nombrecomercial";
                    parametros.put("nombrecomercial", razon.toUpperCase() + "%");
                }
                if (!((ruc == null) || (ruc.isEmpty()))) {
                    where += " and o.ruc like :ruc";
                    parametros.put("ruc", ruc.toUpperCase() + "%");
                }
                int total = 0;

                try {
                    parametros.put(";where", where);
                    total = ejbEmpresa.contar(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                int endIndex = i + pageSize;
                if (endIndex > total) {
                    endIndex = total;
                }

                parametros.put(";inicial", i);
                parametros.put(";final", endIndex);
                empresas.setRowCount(total);
                try {
                    return ejbEmpresa.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        return null;
    }

    protected String nuevo() {
        empresa = new Empresas();
        formulario.insertar();
        telefono1 = new Telefonos();
        telefono2 = new Telefonos();
        telefono3 = new Telefonos();
        return null;
    }

    protected String modifica(Empresas empresa) {
        this.empresa = empresa;
        formulario.editar();
        telefono1 = empresa.getTelefono1();
        telefono3 = empresa.getTelefono2();
        telefono2 = empresa.getCelular();
        if (telefono1 == null) {
            telefono1 = new Telefonos();
        }
        if (telefono2 == null) {
            telefono2 = new Telefonos();
        }
        if (telefono3 == null) {
            telefono3 = new Telefonos();
        }
        return null;
    }

    protected String elimina(Empresas empresa) {
        this.empresa = empresa;
        telefono1 = empresa.getTelefono1();
        telefono3 = empresa.getTelefono2();
        telefono2 = empresa.getCelular();
        formulario.eliminar();
        return null;
    }

    protected String insertar() {
        if (validar()) {
            return null;
        }
        try {
            // var si ya hay el ruc

            ejbTelefonos.create(telefono1, seguridadbean.getLogueado().getUserid());
            ejbTelefonos.create(telefono2, seguridadbean.getLogueado().getUserid());
            ejbTelefonos.create(telefono3, seguridadbean.getLogueado().getUserid());
            empresa.setCelular(telefono2);
            empresa.setTelefono1(telefono1);
            empresa.setTelefono2(telefono3);
            empresa.setEstado(Boolean.TRUE);
            ejbEmpresa.create(empresa, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(EmpresasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
//        formulario.cancelar();
//        buscar();
        return null;
    }

    protected String grabarSolo() {
        try {
            ejbEmpresa.edit(empresa, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            Logger.getLogger(EmpresasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    protected String grabar() {
        if (validar()) {
            return null;
        }
        try {
            if (telefono1.getId() == null) {
                ejbTelefonos.create(telefono1, seguridadbean.getLogueado().getUserid());
            } else {
                ejbTelefonos.edit(telefono1, seguridadbean.getLogueado().getUserid());
            }
            if (telefono2.getId() == null) {
                ejbTelefonos.create(telefono2, seguridadbean.getLogueado().getUserid());
            } else {
                ejbTelefonos.edit(telefono2, seguridadbean.getLogueado().getUserid());
            }
            if (telefono3.getId() == null) {
                ejbTelefonos.create(telefono3, seguridadbean.getLogueado().getUserid());
            } else {
                ejbTelefonos.edit(telefono3, seguridadbean.getLogueado().getUserid());
            }
            empresa.setCelular(telefono2);
            empresa.setTelefono1(telefono1);
            empresa.setTelefono2(telefono3);
            empresa.setEstado(Boolean.TRUE);
            ejbEmpresa.edit(empresa, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(EmpresasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
//        formulario.cancelar();
//        buscar();
        return null;
    }

    public String eliminar() {
//        if (validar()) {
//            return null;
//        }
        try {
            empresa.setEstado(Boolean.FALSE);
            Telefonos t = empresa.getCelular();
            Telefonos t1 = empresa.getTelefono1();
            Telefonos t2 = empresa.getTelefono2();
            ejbEmpresa.remove(empresa, seguridadbean.getLogueado().getUserid());
            if (t != null) {
                ejbTelefonos.remove(t, seguridadbean.getLogueado().getUserid());
            }
            if (t1 != null) {
                ejbTelefonos.remove(t1, seguridadbean.getLogueado().getUserid());
            }
            if (t2 != null) {
                ejbTelefonos.remove(t2, seguridadbean.getLogueado().getUserid());
            }
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(EmpresasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    /**
     * @return the telefono1
     */
    public Telefonos getTelefono1() {
        return telefono1;
    }

    /**
     * @param telefono1 the telefono1 to set
     */
    public void setTelefono1(Telefonos telefono1) {
        this.telefono1 = telefono1;
    }

    /**
     * @return the telefono2
     */
    public Telefonos getTelefono2() {
        return telefono2;
    }

    /**
     * @param telefono2 the telefono2 to set
     */
    public void setTelefono2(Telefonos telefono2) {
        this.telefono2 = telefono2;
    }

    /**
     * @return the telefono3
     */
    public Telefonos getTelefono3() {
        return telefono3;
    }

    /**
     * @param telefono3 the telefono3 to set
     */
    public void setTelefono3(Telefonos telefono3) {
        this.telefono3 = telefono3;
    }

//    public void cambiaempresa(ValueChangeEvent event) {
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
//                return;
//            }
//            List<Empresas> aux = new LinkedList<Empresas>();
//            // traer la consulta
//            Map parametros = new HashMap();
//            String where = " o.proveedores is not null and o.estado=:estado ";
//            int tbuscar = 0;
//            if (getTipobuscar() != null) {
//                tbuscar = Integer.parseInt(getTipobuscar());
//            }
//            if (tbuscar > 0) {//cleinte
//                where = " o.clientes is  not null and o.estado=:estado";
//            } else if (tbuscar < 0) {//proveedor
//                where = " o.proveedores is not null and o.estado=:estado";
//            }
//            parametros.put("estado", true);
//            if (Math.abs(tbuscar) == 1) {//ruc
//                where += " and o.ruc like :ruc";
//                parametros.put("ruc", newWord + "%");
//                parametros.put(";orden", "o.ruc");
//            } else if (Math.abs(tbuscar) == 2) {//nombre
//                where += " and upper(o.nombre) like :nombre";
//                parametros.put("nombre", newWord.toUpperCase() + "%");
//                parametros.put(";orden", "o.emsnombre");
//            } else if (Math.abs(tbuscar) == 3) {//reazon
//                where += " and upper(o.nombrecomercial) like :comercial";
//                parametros.put("comercial", newWord.toUpperCase() + "%");
//                parametros.put(";orden", "o.nombrecomercial");
//            } else if (Math.abs(tbuscar) == 4) {//reazon
//                where += " and upper(o.proveedores.codigo) like :codigo";
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
//                aux = ejbEmpresa.encontarParametros(parametros);
//            } catch (ConsultarException ex) {
//                Logger.getLogger(EmpresasBean.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            setLbuscar(new ArrayList());
//            for (Empresas e : aux) {
//                String texto = "";
//                if (Math.abs(tbuscar) == 1) {//ruc
//                    texto = e.getRuc();
//                } else if (Math.abs(tbuscar) == 2) {//nombre
//                    texto = e.getNombre();
//                } else if (Math.abs(tbuscar) == 3) {//reazon
//                    texto = e.getNombrecomercial();
//                } else if (Math.abs(tbuscar) == 4) {//codigo
//                    texto = e.getProveedores().getCodigo();
//                }
//
//                SelectItem s = new SelectItem(e, texto);
//                getLbuscar().add(s);
//            }
//            if (autoComplete.getSelectedItem() != null) {
//                setEmpresa((Empresas) autoComplete.getSelectedItem().getValue());
//                sicronizarProveedor();
//            } else {
//
//                Empresas tmp = null;
//                for (int i = 0, max = getLbuscar().size(); i < max; i++) {
//                    SelectItem e = (SelectItem) getLbuscar().get(i);
//                    if (e.getLabel().compareToIgnoreCase(newWord) == 0) {
//                        tmp = (Empresas) e.getValue();
//                    }
//
//                }
//                if (tmp != null) {
//                    setEmpresa(tmp);
//                    sicronizarProveedor();
//                }
//            }
//
//        }
//    }
    /**
     * @return the tipobuscar
     */
    public String getTipobuscar() {
        return tipobuscar;
    }

    /**
     * @param tipobuscar the tipobuscar to set
     */
    public void setTipobuscar(String tipobuscar) {
        this.tipobuscar = tipobuscar;
    }

    private void sicronizarProveedor() {
        if (tipoen == 0) {
            if (empresa.getProveedores() == null) {
                Map parametros = new HashMap();
                parametros.put(";where", "o.empresa=:empresa");
                parametros.put("empresa", empresa);
                try {
                    List<Proveedores> lp = ejbProveedores.encontarParametros(parametros);
                    for (Proveedores p : lp) {
                        empresa.setProveedores(p);
                    }
                } catch (ConsultarException ex) {
                    Logger.getLogger(ProveedoresBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public Empresas taerRuc(String ruc) {
        if ((ruc == null) || (ruc.isEmpty())) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.ruc=:ruc");
        parametros.put("ruc", ruc);
        try {
            List<Empresas> le = ejbEmpresa.encontarParametros(parametros);
            for (Empresas e : le) {
                return e;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(EmpresasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
