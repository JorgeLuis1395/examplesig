/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

//import com.icesoft.faces.component.selectinputtext.SelectInputText;
//import com.icesoft.faces.component.selectinputtext.TextChangeEvent;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.Serializable;
import java.util.ArrayList;
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
import org.beans.sfccbdmq.OrganigramaFacade;
import org.entidades.sfccbdmq.Organigrama;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "organigrama")
@ViewScoped
public class OrganigramaBean implements Serializable {

    /**
     * Creates a new instance of MaestrosBean
     */
    public OrganigramaBean() {
        organigramas = new LazyDataModel<Organigrama>() {
            @Override
            public List<Organigrama> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                //
                return null;
            }
        };
    }
    private LazyDataModel<Organigrama> organigramas;
    private Organigrama organigrama;
    private int tamano;
    private Organigrama anterior;
    private Perfiles perfil;
    @EJB
    private OrganigramaFacade ejbOrganigrama;
    //Autocompletar
    private Formulario formulario = new Formulario();
    private Formulario formularioSuperior = new Formulario();
    private List listaOrganigrama;
    private String nombre;
    private String codigo;
    private String codigoAnterior;
    private Organigrama organigramaSeleccionada;
    private Organigrama organigramaSeleccionadaEdicion;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    private List<Organigrama> listaAutocompletar = new ArrayList<>();
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

    public String buscar() {
        setOrganigramas(new LazyDataModel<Organigrama>() {
            @Override
            public List<Organigrama> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                if (scs.length == 0) {
                    parametros.put(";orden", "o.codigo asc,o.ordinal asc");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }
                String where = " o.activo=true ";
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }
                if (!((nombre == null) || (nombre.isEmpty()))) {
                    where += " and upper(o.nombre) like :nombre";
                    parametros.put("nombre", "%"+nombre.toUpperCase() + "%");
                }
                if (!((codigo == null) || (codigo.isEmpty()))) {
                    where += " and upper(o.codigo) like :codigo";
                    parametros.put("codigo","%"+ codigo.toUpperCase() + "%");
                }
                parametros.put(";where", where);
                int total = 0;
                try {
                    total = ejbOrganigrama.contar(parametros);
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
                try {
                    organigramas.setRowCount(total);

                    return ejbOrganigrama.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        });
        return null;
    }
    // acciones de base de datos

    public Organigrama traer(Integer id) throws ConsultarException, ConsultarException, ConsultarException {
        return ejbOrganigrama.find(id);
    }

    /**
     * @return the organigrama
     */
    public Organigrama getOrganigrama() {
        return organigrama;
    }

    /**
     * @param organigrama the organigrama to set
     */
    public void setOrganigrama(Organigrama organigrama) {
        this.organigrama = organigrama;
    }

    /**
     * @return the tamano
     */
    public int getTamano() {
        return tamano;
    }

    /**
     * @param tamano the tamano to set
     */
    public void setTamano(int tamano) {
        this.tamano = tamano;
    }

    /**
     * @return the organigramas
     */
    public LazyDataModel<Organigrama> getOrganigramas() {
        return organigramas;
    }

    /**
     * @param organigramas the organigramas to set
     */
    public void setOrganigramas(LazyDataModel<Organigrama> organigramas) {
        this.organigramas = organigramas;
    }



    public SelectItem[] getComboOrganigrama() {
        Map parametros = new HashMap();
        String where = "";
        where += " o.activo=true ";
        parametros.put(";where", where);
        parametros.put(";orden", " o.codigo");

        try {
            return Combos.getSelectItems(ejbOrganigrama.encontarParametros(parametros), false);
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboOrganigramaV() {
        Map parametros = new HashMap();
        String where = "";
        where += " o.activo=true ";
        parametros.put(";where", where);
        parametros.put(";orden", " o.codigo");
        try {
            return Combos.getSelectItems(ejbOrganigrama.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public SelectItem[] getComboOrganigramaCajaChica() {
        Map parametros = new HashMap();
        String where = "";
        where += " o.activo=true and o.cajachica=true";
        parametros.put(";where", where);
        parametros.put(";orden", " o.codigo");
        try {
            return Combos.getSelectItems(ejbOrganigrama.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public SelectItem[] getComboDirecciones() {
        Map parametros = new HashMap();
        String where = "";
        where += " o.activo=true and o.esdireccion=true";
        parametros.put(";where", where);
        parametros.put(";orden", " o.codigo");
        try {
            return Combos.getSelectItems(ejbOrganigrama.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the listaOrganigrama
     */
    public List getListaOrganigrama() {
        return listaOrganigrama;
    }

    /**
     * @param listaOrganigrama the listaOrganigrama to set
     */
    public void setListaOrganigrama(List listaOrganigrama) {
        this.listaOrganigrama = listaOrganigrama;
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
     * @return the organigramaSeleccionada
     */
    public Organigrama getOrganigramaSeleccionada() {
        return organigramaSeleccionada;
    }

    /**
     * @param organigramaSeleccionada the organigramaSeleccionada to set
     */
    public void setOrganigramaSeleccionada(Organigrama organigramaSeleccionada) {
        this.organigramaSeleccionada = organigramaSeleccionada;
    }

    public Organigrama tarerCodigo(String codigo) {
        if ((codigo == null) || (codigo.isEmpty())) {
            return null;
        }
        String where = "upper(o.codigo) =:codigo and o.activo=true";
        Map parametros = new HashMap();
        parametros.put("codigo", codigo.toUpperCase());
        parametros.put(";orden", "codigo");
        parametros.put(";where", where);
        try {
            List<Organigrama> ul = ejbOrganigrama.encontarParametros(parametros);
            if ((ul == null) || (ul.isEmpty())) {
                return null;
            }
            return ul.get(0);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("UBICACION").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the anterior
     */
    public Organigrama getAnterior() {
        return anterior;
    }

    /**
     * @param anterior the anterior to set
     */
    public void setAnterior(Organigrama anterior) {
        this.anterior = anterior;
    }

    public String nuevo() {
        // se deberia chequear perfil?
        //solo primer nivel
        organigrama = new Organigrama();
        anterior = null;
        organigrama.setCajachica(Boolean.FALSE);
        getFormulario().insertar();
        return null;
    }

    public String crearHijo(Organigrama padre) {
        // se deberia chequear perfil?
        // ver si formato aplica
        anterior = padre;
        organigrama = new Organigrama();
        organigrama.setSuperior(anterior);
        organigrama.setCajachica(Boolean.FALSE);
        getFormulario().insertar();
        return null;
    }

    public String modificar() {
        organigrama = (Organigrama) getOrganigramas().getRowData();
        getFormulario().editar();
        return null;
    }
    public String modificarSuperior() {
        organigrama = (Organigrama) getOrganigramas().getRowData();
        codigoAnterior=organigrama.getCodigo();
        getFormularioSuperior().editar();
        return null;
    }
    public String eliminar() {
        organigrama = (Organigrama) getOrganigramas().getRowData();
        getFormulario().eliminar();
        return null;
    }

    public String cancelar() {
        getFormulario().cancelar();
        buscar();
        return null;
    }

    private boolean validar() {
        if ((organigrama.getCodigo() == null) || (organigrama.getCodigo().isEmpty())) {
            MensajesErrores.advertencia("Es necesario código");
            return true;
        }
//        if ((organigrama.getCodigoalterno()== null) || (organigrama.getCodigoalterno().isEmpty())) {
//            MensajesErrores.advertencia("Es necesario código alterno");
//            return true;
//        }
        if ((organigrama.getNombre() == null) || (organigrama.getNombre().isEmpty())) {
            MensajesErrores.advertencia("Es necesario nombre");
            return true;
        }

        return false;
    }

    public String insertar() {

        if (validar()) {
            return null;
        }
        try {
//            organigrama.setCodigo(organigrama.getCodigo());
            organigrama.setActivo(Boolean.TRUE);
            ejbOrganigrama.create(organigrama, getParametrosSeguridad().getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        getFormulario().cancelar();
        buscar();
        return null;
    }

    public String grabar() {
//        if (!menusBean.getPerfil().getModificacion()) {
//            MensajesErrores.advertencia("No tiene autorización para modificar un registro");
//        }
        if (validar()) {
            return null;
        }
        try {
            organigrama.setActivo(Boolean.TRUE);
            ejbOrganigrama.edit(organigrama, getParametrosSeguridad().getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        getFormulario().cancelar();
        buscar();
        return null;
    }
    public String grabarSuperior() {
//        if (!menusBean.getPerfil().getModificacion()) {
//            MensajesErrores.advertencia("No tiene autorización para modificar un registro");
//        }
       
        try {
            organigrama.setActivo(Boolean.TRUE);
            ejbOrganigrama.edit(organigrama, getParametrosSeguridad().getLogueado().getUserid());
            Map parametros=new HashMap();
            parametros.put(";where", "o.superior=:superior");
            parametros.put("superior", organigrama);
            List<Organigrama> listaHijos=ejbOrganigrama.encontarParametros(parametros);
            for (Organigrama o:listaHijos){
                String codigoCambia=o.getCodigo();
                o.setCodigo(codigoCambia.replace(codigoAnterior, organigrama.getCodigo()));
                ejbOrganigrama.edit(o, getParametrosSeguridad().getLogueado().getUserid());
            }
        } catch (GrabarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        getFormularioSuperior().cancelar();
        buscar();
        return null;
    }
    public String borrar() {
//        if (!menusBean.getPerfil().getBorrado()) {
//            MensajesErrores.advertencia("No tiene autorización para borrar un registro");
//        }
        try {
            organigrama.setActivo(Boolean.FALSE);
            ejbOrganigrama.edit(organigrama, getParametrosSeguridad().getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }

        getFormulario().cancelar();
        buscar();
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
     * @return the listaAutocompletar
     */
    public List<Organigrama> getListaAutocompletar() {
        return listaAutocompletar;
    }

    /**
     * @param listaAutocompletar the listaAutocompletar to set
     */
    public void setListaAutocompletar(List<Organigrama> listaAutocompletar) {
        this.listaAutocompletar = listaAutocompletar;
    }

    /**
     * @return the organigramaSeleccionadaEdicion
     */
    public Organigrama getOrganigramaSeleccionadaEdicion() {
        return organigramaSeleccionadaEdicion;
    }

    /**
     * @param organigramaSeleccionadaEdicion the organigramaSeleccionadaEdicion
     * to set
     */
    public void setOrganigramaSeleccionadaEdicion(Organigrama organigramaSeleccionadaEdicion) {
        this.organigramaSeleccionadaEdicion = organigramaSeleccionadaEdicion;
    }

    /**
     * @return the codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
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
        String nombreForma = "OrganigramaVista";
        if (perfilString == null) {
            MensajesErrores.fatal("Sin perfil válido");
            getParametrosSeguridad().cerraSession();
        }
        this.setPerfil(getParametrosSeguridad().traerPerfil(perfilString));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil válido");
            getParametrosSeguridad().cerraSession();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguridad.getGrpUsuario().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                getParametrosSeguridad().cerraSession();
//            }
//        }
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
     * @return the formularioSuperior
     */
    public Formulario getFormularioSuperior() {
        return formularioSuperior;
    }

    /**
     * @param formularioSuperior the formularioSuperior to set
     */
    public void setFormularioSuperior(Formulario formularioSuperior) {
        this.formularioSuperior = formularioSuperior;
    }
    
}
