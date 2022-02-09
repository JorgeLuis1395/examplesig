/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.compras.sfccbdmq.ProveedoresBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import java.io.Serializable;
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
import org.beans.sfccbdmq.CargosFacade;
import org.beans.sfccbdmq.ConceptosFacade;
import org.entidades.sfccbdmq.Cargos;
import org.entidades.sfccbdmq.Conceptos;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;



@ManagedBean(name = "conceptosSfccbdmq")
@ViewScoped
public class ConceptosBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of TipoConceptoBean
     */
    public ConceptosBean() {
    }

    private Formulario formulario = new Formulario();
    private Conceptos concepto;
    private List<Conceptos> listaConceptos;
    private String nombre;
    private Integer tipo;
    private boolean copia;
    
    @EJB
    private ConceptosFacade ejbConceptos;
    @EJB
    private CargosFacade ejbCargos;
    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedorBean;
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
    /* @return the parametrosSeguridad
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

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            return;
        }
        String nombreForma = "ConceptosVista";
        if (perfilString == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
        this.setPerfil(parametrosSeguridad.traerPerfil(perfilString));
        if (this.getPerfil() == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguridad.getGrpUsuario().getGrupo())) {
//                org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                parametrosSeguridad.cerraSession();
//            }
//        }
    }
    // fin perfiles

    public String nuevo() {

        concepto = new Conceptos();
        formulario.insertar();
        proveedorBean.setEmpresa(null);
        proveedorBean.setRuc(null);
        return null;
    }

    public String modificar() {
        concepto = listaConceptos.get(formulario.getFila().getRowIndex());
        proveedorBean.setProveedor(null);
        proveedorBean.setEmpresa(null);
        proveedorBean.setRuc(null);
        if (concepto.getProveedor() != null) {
            proveedorBean.setProveedor(concepto.getProveedor());
            proveedorBean.setEmpresa(concepto.getProveedor().getEmpresa());
            proveedorBean.setRuc(concepto.getProveedor().getEmpresa().getRuc());
        }
        formulario.editar();
        return null;
    }

    public String eliminar() {
        concepto = listaConceptos.get(formulario.getFila().getRowIndex());
        if (concepto.getProveedor() != null) {
            proveedorBean.setEmpresa(concepto.getProveedor().getEmpresa());
            proveedorBean.setRuc(concepto.getProveedor().getEmpresa().getRuc());
        }
        formulario.eliminar();
        return null;
    }

    public String cancelar() {
        formulario.cancelar();
        proveedorBean.setEmpresa(null);
        proveedorBean.setRuc(null);
        buscar();
        return null;
    }

    public String buscar() {
        try {
            Map parametros = new HashMap();
            String where = "o.activo=true";

            //NOMBRE
            if (!((nombre == null) || (nombre.isEmpty()))) {
                where += " and upper(o.nombre) like:nombre";
                parametros.put("nombre", "%" + nombre.toUpperCase() + "%");
            }
//            if (tipo > -10) {
//                if (!where.isEmpty()) {
//                    where += " and ";
//                }
//                where += " o.tipo =:tipo";
//                parametros.put("tipo", tipo);
//            }

            parametros.put(";where", where);
            parametros.put(";orden", "o.nombre asc");
            listaConceptos = ejbConceptos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ConceptosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private boolean validar() {

        if ((concepto.getNombre() == null || concepto.getNombre().isEmpty())) {
            MensajesErrores.advertencia("Nombre es necesario");
            return true;
        }
        if ((concepto.getCodigo() == null || concepto.getCodigo().isEmpty())) {
            MensajesErrores.advertencia("Código es necesario");
            return true;
        }
//        if (concepto.getTipo() == 2) {
//            concepto.setCargovacaciones(Boolean.TRUE);
//        }

        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        if (!((concepto.getFormula() == null) || (concepto.getFormula().isEmpty()))) {
            if (compilar(concepto.getFormula(), concepto.getCodigo())) {
                return null;
            }
        }
        try {
//            if (proveedorBean.getProveedor()!= null) {
//                concepto.setProveedor(proveedorBean.getProveedor());
//            }
            concepto.setProveedor(proveedorBean.getProveedor());
            concepto.setActivo(true);
            ejbConceptos.create(concepto, parametrosSeguridad.getLogueado().getUserid());
            // traer los empleados 
            
        } catch (InsertarException  ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(ConceptosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public boolean compilar(String funcion, String nombre) {
        String retorno;
        try {
            retorno = ejbConceptos.crearSp(funcion, nombre);
            if (retorno != null) {
                MensajesErrores.advertencia("Existen errores en la fórmula : " + retorno);
                return true;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ConceptosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    public String grabar() {
        if (validar()) {
            return null;
        }
        try {
            if (!((concepto.getFormula() == null) || (concepto.getFormula().isEmpty()))) {
                if (compilar(concepto.getFormula(), concepto.getCodigo())) {
                    return null;
                }
            }
            concepto.setActivo(true);
//            if (proveedorBean.getProveedor()!= null) {
//                concepto.setProveedor(proveedorBean.getEmpresa().getProveedores());
//            }
            concepto.setProveedor(proveedorBean.getProveedor());
            ejbConceptos.edit(concepto, parametrosSeguridad.getLogueado().getUserid());
            
        } catch (GrabarException  ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(ConceptosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borrar() {
        try {
            concepto.setActivo(false);
            ejbConceptos.edit(concepto, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(ConceptosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public Conceptos traer(Integer id) throws ConsultarException {
        return ejbConceptos.find(id);
    }

    public SelectItem[] getComboTiposConcepto() {
        buscar();
        return Combos.getSelectItems(listaConceptos, true);
    }

    public SelectItem[] getComboConceptos() {
        try {
            Map parametros = new HashMap();
//            String where = "o.tipo=1";

            parametros.put(";where", "o.activo=true");
            parametros.put(";orden", "o.nombre asc");
            return Combos.getSelectItems(ejbConceptos.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ConceptosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboConceptosNovedades() {
        try {
            Map parametros = new HashMap();
//            String where = "o.tipo=1";

            parametros.put(";where", "o.novedad=true and o.activo=true");
            parametros.put(";orden", "o.nombre asc");
            return Combos.getSelectItems(ejbConceptos.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ConceptosBean.class.getName()).log(Level.SEVERE, null, ex);
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

    /**
     * @return the concepto
     */
    public Conceptos getConcepto() {
        return concepto;
    }

    /**
     * @param concepto the concepto to set
     */
    public void setConcepto(Conceptos concepto) {
        this.concepto = concepto;
    }

    /**
     * @return the listaConceptos
     */
    public List<Conceptos> getListaConceptos() {
        return listaConceptos;
    }

    /**
     * @param listaConceptos the listaConceptos to set
     */
    public void setListaConceptos(List<Conceptos> listaConceptos) {
        this.listaConceptos = listaConceptos;
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
     * @return the tipo
     */
    public Integer getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the copia
     */
    public boolean isCopia() {
        return copia;
    }

    /**
     * @param copia the copia to set
     */
    public void setCopia(boolean copia) {
        this.copia = copia;
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

    public SelectItem[] getComboDeque() {
        int size = 4;
        SelectItem[] items = new SelectItem[size];
        items[0] = new SelectItem(null, "--- Seleccione Uno ---");
        items[1] = new SelectItem(0, "SMV");
        items[2] = new SelectItem(1, "RMU");
        items[3] = new SelectItem(2, "Ingresos");
        return items;
    }

    public String getDeQue(Integer que) {
        if (null != que) switch (que) {
            case 0:
                return "SMV";
            case 1:
                return "RMU";
            case 2:
                return "Ingresos";
            default:
                break;
        }
        return "";
    }

    public List<Conceptos> getIngresosSobre() {
        try {
            Map parametros = new HashMap();
//            String where = "o.tipo=1";

            parametros.put(";where", "o.ingreso=true and o.sobre=true and o.activo=true");
            parametros.put(";orden", "o.nombre asc");
            return ejbConceptos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ConceptosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<Conceptos> getEgresosSobre() {
        try {
            Map parametros = new HashMap();
//            String where = "o.tipo=1";

            parametros.put(";where", "o.ingreso=false and o.sobre=true and o.activo=true");
            parametros.put(";orden", "o.nombre asc");
            return ejbConceptos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ConceptosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<Conceptos> getProviciones() {
        try {
            Map parametros = new HashMap();
//            String where = "o.tipo=1";

            parametros.put(";where", "o.provision=true and o.activo=true");
            parametros.put(";orden", "o.nombre asc");
            return ejbConceptos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ConceptosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboProviciones() {
        try {
            Map parametros = new HashMap();
//            String where = "o.tipo=1";

            parametros.put(";where", "o.provision=true and o.activo=true");
            parametros.put(";orden", "o.nombre asc");
            return Combos.getSelectItems(ejbConceptos.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ConceptosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public SelectItem[] getComboProvicionesSinProveedor() {
        try {
            Map parametros = new HashMap();
//            String where = "o.tipo=1";
            parametros.put(";where", "o.provision=true and o.activo=true and o.proveedor is null");
            parametros.put(";orden", "o.nombre asc");
            return Combos.getSelectItems(ejbConceptos.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ConceptosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public SelectItem[] getComboRetencion() {
        try {
            Map parametros = new HashMap();
//            String where = "o.tipo=1";
            parametros.put(";where", "o.retencion=true and o.activo=true");
            parametros.put(";orden", "o.nombre asc");
            return Combos.getSelectItems(ejbConceptos.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ConceptosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public SelectItem[] getComboConceptosEgresos() {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.activo=true and o.ingreso=false");
            parametros.put(";orden", "o.nombre asc");
            return Combos.getSelectItems(ejbConceptos.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ConceptosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
