/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.seguridad.sfccbdmq;

//import com.icesoft.faces.component.selectinputtext.SelectInputText;
import org.auxiliares.sfccbdmq.Codificador;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.util.ArrayList;
import java.util.Date;
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
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import org.beans.sfccbdmq.EntidadesFacade;
import org.entidades.sfccbdmq.Entidades;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.icefaces.ace.component.autocompleteentry.AutoCompleteEntry;
import org.icefaces.ace.event.TextChangeEvent;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "entidadesSfccbdmq")
@ViewScoped
public class EntidadesBean {

    /**
     * Creates a new instance of EntidadesBean
     */
    public EntidadesBean() {
        entidades = new LazyDataModel<Entidades>() {
            @Override
            public List<Entidades> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                //
                return null;
            }
        };
    }
    private LazyDataModel<Entidades> entidades;
    private List<Entidades> listaEntidades;
    private Entidades entidad;
    private Entidades existente;
    private Formulario formulario = new Formulario();
    private Formulario formularioExiste = new Formulario();
    @EJB
    private EntidadesFacade ejbEntidades;
    private String cedula;
    private String apellidos;
    private String nombres;
    private String email;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    private List listaUsuarios;
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
        String nombreForma = "EntidadesVista";
        if (perfil == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
        if (!((perfil == null) || (perfil.isEmpty()))){
            this.perfil = seguridadbean.traerPerfil(perfil);
        }

        if (this.getPerfil() == null) {
            return;
//            MensajesErrores.fatal("Sin perfil válido");
//            seguridadbean.cerraSession();
        }
        if (this.perfil.getMenu().equals("PER")) {
            return;
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
     * @return the entidades
     */
    public LazyDataModel<Entidades> getEntidades() {
        return entidades;
    }

    /**
     * @param entidades the entidades to set
     */
    public void setEntidades(LazyDataModel<Entidades> entidades) {
        this.entidades = entidades;
    }

    /**
     * @return the entidad
     */
    public Entidades getEntidad() {
        return entidad;
    }

    /**
     * @param entidad the entidad to set
     */
    public void setEntidad(Entidades entidad) {
        this.entidad = entidad;
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
     * @return the cedula
     */
    public String getCedula() {
        return cedula;
    }

    /**
     * @param cedula the cedula to set
     */
    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    /**
     * @return the apellidos
     */
    public String getApellidos() {
        return apellidos;
    }

    /**
     * @param apellidos the apellidos to set
     */
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    /**
     * @return the nombres
     */
    public String getNombres() {
        return nombres;
    }

    /**
     * @param nombres the nombres to set
     */
    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    public String crear() {
        // se deberia chequear perfil?
//        if (!menusBean.getPerfil().getNuevo()) {
//            MensajesErrores.advertencia("No tiene autorización para crear un registro");
//            return null;
//        }
        entidad = new Entidades();
        formulario.insertar();
        return null;
    }

    public String modificar() {
//        if (!menusBean.getPerfil().getModificacion()) {
//            MensajesErrores.advertencia("No tiene autorización para modificar un registro");
//            return null;
//        }
        entidad = (Entidades) entidades.getRowData();
        formulario.editar();
        return null;
    }

    public String eliminar() {
//        if (!menusBean.getPerfil().getBorrado()) {
//            MensajesErrores.advertencia("No tiene autorización para borrar un registro");
//            return null;
//        }

        entidad = (Entidades) entidades.getRowData();
        formulario.eliminar();
        return null;
    }

    public String buscar() {
//        if (!menusBean.getPerfil().getConsulta()) {
//            MensajesErrores.advertencia("No tiene autorización para consultar");
//            return null;
//        }
        entidades = new LazyDataModel<Entidades>() {
            @Override
            public List<Entidades> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                if (scs.length == 0) {
                    parametros.put(";orden", "o.apellidos");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }
                String where = "  o.activo=true ";
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }
                if (!((apellidos == null) || (apellidos.isEmpty()))) {
                    where += " and upper(o.apellidos) like :apellidos";
                    parametros.put("apellidos", apellidos.toUpperCase() + "%");
                }
                if (!((nombres == null) || (nombres.isEmpty()))) {
                    where += " and upper(o.nombres) like :nombres";
                    parametros.put("nombres", nombres.toUpperCase() + "%");
                }
                if (!((cedula == null) || (cedula.isEmpty()))) {
                    where += " and upper(o.pin) like :cedula";
                    parametros.put("cedula", cedula.toUpperCase() + "%");
                }
                if (!((email == null) || (email.isEmpty()))) {
                    where += " and upper(o.email) like :email";
                    parametros.put("email", email.toUpperCase() + "%");
                }
                int total = 0;

                try {
                    parametros.put(";where", where);
                    total = ejbEntidades.contar(parametros);
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
                entidades.setRowCount(total);
                try {
                    return ejbEntidades.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };

        return null;

    }

    private boolean validar() {
        if ((entidad.getApellidos() == null) || (entidad.getApellidos().isEmpty())) {
            MensajesErrores.advertencia("Apellidos es obligatorio");
            return true;
        }
        if ((entidad.getUserid() == null) || (entidad.getUserid().isEmpty())) {
            MensajesErrores.advertencia("User ID es obligatorio");
            return true;
        }
        if ((entidad.getNombres() == null) || (entidad.getNombres().isEmpty())) {
            MensajesErrores.advertencia("Nombres es obligatorio");
            return true;
        }
        if ((entidad.getPin() == null) || (entidad.getPin().isEmpty())) {
            MensajesErrores.advertencia("CI o RUC es obligatorio");
            return true;
        }
        if ((entidad.getEmail() == null) || (entidad.getEmail().isEmpty())) {
            MensajesErrores.advertencia("email es obligatorio");
            return true;
        }
        if ((entidad.getFecha() == null)) {
            MensajesErrores.advertencia("Fecha nacimiento obligatorio");
            return true;
        }
        if ((entidad.getFecha().after(new Date()))) {
            MensajesErrores.advertencia("Fecha nacimiento menor a hoy");
            return true;
        }
//        if ((getUbicacionBean().getUbicacionSeleccionada() == null)) {
//            MensajesErrores.advertencia("Ubicación es requerida");
//            return true;
//        }
        return false;
    }

    public String insertar() {
//        if (!menusBean.getPerfil().getNuevo()) {
//            MensajesErrores.advertencia("No tiene autorización para crear un registro");
//            return null;
//        }
        if (getFormularioExiste().isNuevo()) {
            MensajesErrores.advertencia("Registro ya existe, no se puede crear con CI repetido");
            return null;
        }
        if (getFormularioExiste().isBorrar()) {
            MensajesErrores.advertencia("Registro ya existe, no se puede crear con CI en este nuevo rol");
            return null;
        }
        if (validar()) {
            return null;
        }

        try {
            entidad.setActivo(Boolean.TRUE);
            Codificador cod = new Codificador();
            entidad.setPwd(cod.getEncoded(entidad.getPin(), "MD5"));
            ejbEntidades.create(entidad, seguridadbean.getLogueado().getUserid());

        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String grabar() {
//        if (!menusBean.getPerfil().getModificacion()) {
//            MensajesErrores.advertencia("No tiene autorización para modificar un registro");
//            return null;
//        }
        if (validar()) {
            return null;
        }
        try {

            entidad.setActivo(Boolean.TRUE);
            ejbEntidades.edit(entidad, getSeguridadbean().getLogueado().getUserid());

        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borrar() {
//        if (!menusBean.getPerfil().getBorrado()) {
//            MensajesErrores.advertencia("No tiene autorización para borrar un registro");
//            return null;
//        }
        try {
            entidad.setActivo(Boolean.FALSE);
            ejbEntidades.edit(entidad, getSeguridadbean().getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }

        formulario.cancelar();
        buscar();
        return null;
    }
    // falta el combo

    public Entidades traer(Integer id) throws ConsultarException {
        return ejbEntidades.find(id);
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
     * @return the formularioExiste
     */
    public Formulario getFormularioExiste() {
        return formularioExiste;
    }

    /**
     * @param formularioExiste the formularioExiste to set
     */
    public void setFormularioExiste(Formulario formularioExiste) {
        this.formularioExiste = formularioExiste;
    }

    public Entidades traerCedula(String cedula) {
        if (cedula == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.pin=:cedula ");
        parametros.put("cedula", cedula);
        try {
            List<Entidades> el = ejbEntidades.encontarParametros(parametros);
            if (!((el == null) || (el.isEmpty()))) {
                return el.get(0);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(EntidadesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public void cambiaCedula(ValueChangeEvent event) {
        // cambia el texto de la cedula
        String nuevaCedula = (String) event.getNewValue();
        // consultar por cedula 
        List<Entidades> aux = new LinkedList<>();
        // traer la consulta
        Map parametros = new HashMap();
        String where = " o.pin=:pin";
        parametros.put("pin", nuevaCedula);
        parametros.put(";where", where);
        try {
            aux = ejbEntidades.encontarParametros(parametros);
            if ((aux == null) || (aux.isEmpty())) {
                formularioExiste.cancelar();
            } else {
                existente = aux.get(0);
                formularioExiste.insertar();
            }
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }

    }

    public void cambiaApellido(ValueChangeEvent event) {
        entidad = null;
        if (listaEntidades == null) {
            return;
        }
        String newWord = (String) event.getNewValue();
        for (Entidades e : listaEntidades) {
            String acomparar=e.toString();
            if (acomparar.compareToIgnoreCase(newWord) == 0) {
                entidad = e;
            }
        }

    }
//    public void cambiaApellido(ValueChangeEvent event) {
//        entidad = null;
//        if (event.getComponent() instanceof AutoCompleteEntry) {
//            // get the number of displayable records from the component
//            AutoCompleteEntry autoComplete
//                    = (AutoCompleteEntry) event.getComponent();
//            // get the new value typed by component user.
//            String newWord = (String) event.getNewValue();
//            //        getEmpresasBeans().setComercial("");
//
//            List<Entidades> aux = new LinkedList<>();
//            // traer la consulta
//            Map parametros = new HashMap();
//            String where = " o.activo=true";
//            where += " and  upper(o.apellidos) like :apellidos";
//            parametros.put("apellidos", newWord.toUpperCase() + "%");
//            parametros.put(";where", where);
//            parametros.put(";orden", " o.apellidos");
//            int total = 15;
////            int total = ((AutoCompleteEntry) event.getComponent()).getRows();
//            // Contadores
//            parametros.put(";inicial", 0);
//            parametros.put(";final", total);
//            try {
//                aux = ejbEntidades.encontarParametros(parametros);
//            } catch (ConsultarException ex) {
//                MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
//                Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
//            }
//            setListaUsuarios(new ArrayList());
//            for (Entidades e : aux) {
//                SelectItem s = new SelectItem(e, e.getApellidos());
//                getListaUsuarios().add(s);
//            }
//            if (autoComplete.getSubmittedValue()!= null) {
//                entidad = (Entidades) autoComplete.getSubmittedValue();
//            } else {
//
//                Entidades tmp = null;
//                for (int i = 0, max = getListaUsuarios().size(); i < max; i++) {
//                    SelectItem e = (SelectItem) getListaUsuarios().get(i);
//                    if (e.getLabel().compareToIgnoreCase(newWord) == 0) {
//                        tmp = (Entidades) e.getValue();
//                    }
//
//                }
//                if (tmp != null) {
//                    entidad = tmp;
//                }
//            }
//
//        }
//    }

    /**
     * @return the listaUsuarios
     */
    public List getListaUsuarios() {
        return listaUsuarios;
    }

    /**
     * @param listaUsuarios the listaUsuarios to set
     */
    public void setListaUsuarios(List listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }

    public String aceptarNuevo() {
        // se deberia chequear perfil?
        entidad = getExistente();
        entidad.setActivo(Boolean.TRUE);
        formularioExiste.cancelar();

        formulario.editar();
        return null;
    }

    public String noAceptaNuevo() {
        // se deberia chequear perfil?
//        entidad = existente;
        formulario.cancelar();
        formularioExiste.cancelar();
        return null;
    }

    /**
     * @return the existente
     */
    public Entidades getExistente() {
        return existente;
    }

    /**
     * @param existente the existente to set
     */
    public void setExistente(Entidades existente) {
        this.existente = existente;
    }

    public void completaCedula(ValueChangeEvent event) {
        entidad = null;
        if (event.getComponent() instanceof AutoCompleteEntry) {
            // get the number of displayable records from the component
            AutoCompleteEntry autoComplete
                    = (AutoCompleteEntry) event.getComponent();
            // get the new value typed by component user.
            String newWord = (String) event.getNewValue();
            //        getEmpresasBeans().setComercial("");

            List<Entidades> aux = new LinkedList<>();
            // traer la consulta
            Map parametros = new HashMap();
            String where = " o.activo=true";
            where += " and  o.pin like :cedula";
            parametros.put("cedula", newWord.toUpperCase() + "%");
            parametros.put(";where", where);
            parametros.put(";orden", " o.pin");
            int total = 15;
//            int total = ((SelectInputText) event.getComponent()).getRows();
            // Contadores
            parametros.put(";inicial", 0);
            parametros.put(";final", total);
            try {
                aux = ejbEntidades.encontarParametros(parametros);
            } catch (ConsultarException ex) {
                MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
                Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
            }
            setListaUsuarios(new ArrayList());
            for (Entidades e : aux) {
                SelectItem s = new SelectItem(e, e.getPin());
                getListaUsuarios().add(s);
            }
            if (autoComplete.getSubmittedValue() != null) {
                entidad = (Entidades) autoComplete.getSubmittedValue();
            } else {

                Entidades tmp = null;
                for (int i = 0, max = getListaUsuarios().size(); i < max; i++) {
                    SelectItem e = (SelectItem) getListaUsuarios().get(i);
                    if (e.getLabel().compareToIgnoreCase(newWord) == 0) {
                        tmp = (Entidades) e.getValue();
                    }

                }
                if (tmp != null) {
                    entidad = tmp;
                }
            }

        }
    }

    /**
     * @return the listaEntidades
     */
    public List<Entidades> getListaEntidades() {
        return listaEntidades;
    }

    /**
     * @param listaEntidades the listaEntidades to set
     */
    public void setListaEntidades(List<Entidades> listaEntidades) {
        this.listaEntidades = listaEntidades;
    }

    public void entidadChangeEventHandler(TextChangeEvent event) {
        entidad=null;
        String codigoBuscar = event.getNewValue() != null ? (String) event.getNewValue() : "";
        Map parametros = new HashMap();
        parametros.put(";orden", "o.apellidos");
//        String where = "  o.activo=true ";
        String where = "  o.id is not null";
        where += " and upper(o.apellidos) like :codigo";
        parametros.put("codigo", codigoBuscar.toUpperCase() + "%");
        parametros.put(";inicial", 0);
        parametros.put(";final", 50);
        parametros.put(";where", where);
        try {
            listaEntidades = ejbEntidades.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }

    }

    public void exEmpleadoChangeEventHandler(TextChangeEvent event) {

        String codigoBuscar = event.getNewValue() != null ? (String) event.getNewValue() : "";
        Map parametros = new HashMap();
        parametros.put(";orden", "o.apellidos");
        String where = "  o.activo=true ";
        where += " and upper(o.apellidos) like :codigo";
        parametros.put("codigo", codigoBuscar.toUpperCase() + "%");
        parametros.put(";inicial", 0);
        parametros.put(";final", 15);
        parametros.put(";where", where);
        try {
            listaEntidades = ejbEntidades.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }

    }
}