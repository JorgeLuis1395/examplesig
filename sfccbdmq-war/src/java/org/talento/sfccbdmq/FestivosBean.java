/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import java.io.Serializable;
import java.util.Calendar;
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
import org.beans.sfccbdmq.FestivosFacade;
import org.entidades.sfccbdmq.Festivos;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;



@ManagedBean(name = "festivosSfccbdmq")
@ViewScoped
public class FestivosBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of TipoFestivoBean
     */
    public FestivosBean() {
        festivos = new LazyDataModel<Festivos>() {

            @Override
            public List<Festivos> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }

    private Formulario formulario = new Formulario();
    private Festivos festivoEntidad;
    private LazyDataModel<Festivos> festivos;
    private Date desde;
    private Date hasta;
    private Integer tipo;

    @EJB
    private FestivosFacade ejbFestivos;

    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
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
        setHasta(new Date());
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, 0);
        c.set(Calendar.DATE, 1);
        setDesde(c.getTime());
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
        String nombreForma = "FestivosVista";
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

        setFestivoEntidad(new Festivos());
        getFestivoEntidad().setFecha(new Date());
        getFormulario().insertar();
        return null;
    }

    public String modificar() {
        setFestivoEntidad((Festivos) festivos.getRowData());
        getFormulario().editar();
        return null;
    }

    public String eliminar() {
        setFestivoEntidad((Festivos) festivos.getRowData());
        getFormulario().eliminar();
        return null;
    }

    public String cancelar() {
        getFormulario().cancelar();
        buscar();
        return null;
    }

    public String buscar() {
        festivos = new LazyDataModel<Festivos>() {
            @Override
            public List<Festivos> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();

                try {
                    String where = "o.fecha between :desde and :hasta";
                    for (Map.Entry e : map.entrySet()) {
                        String clave = (String) e.getKey();
                        String valor = (String) e.getValue();

                        where += " and upper(o." + clave + ") like :" + clave;
                        parametros.put(clave, valor.toUpperCase() + "%");
                    }
                    int total;

                    if (scs.length == 0) {
                        parametros.put(";orden", "o.fecha desc");
                    } else {
                        parametros.put(";orden", "o.empleado." + scs[0].getPropertyName()
                                + (scs[0].isAscending() ? " ASC" : " DESC"));
                    }
                    if (tipo != null) {
                        where += " and o.tipo=:tipo";
                        parametros.put("tipo", tipo);
                    }
                    parametros.put(";where", where);
                    parametros.put("desde", desde);
                    parametros.put("hasta", hasta);
                    total = ejbFestivos.contar(parametros);
                    int endIndex = i + pageSize;
                    if (endIndex > total) {
                        endIndex = total;
                    }

                    parametros.put(";inicial", i);
                    parametros.put(";final", endIndex);
                    festivos.setRowCount(total);
                    return ejbFestivos.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        return null;
    }

    /**
     * @return the hasta
     */
    public Date getHasta() {
        return hasta;
    }

    /**
     * @param hasta the hasta to set
     */
    public void setHasta(Date hasta) {
        this.hasta = hasta;
    }

    private boolean validar() {

        if (getFestivoEntidad().getFecha() == null) {
            MensajesErrores.advertencia("Fecha es obligatorio");
            return true;
        }

        if (getFormulario().isNuevo()) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.fecha=:fecha");
            parametros.put("fecha", getFestivoEntidad().getFecha());
            try {
                int total = ejbFestivos.contar(parametros);
                if (total > 0) {
                    MensajesErrores.advertencia("Festivo ya registrado");
                    return true;
                }
            } catch (ConsultarException ex) {
                Logger.getLogger(FestivosBean.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        if ((getFestivoEntidad().getNombre() == null || getFestivoEntidad().getNombre().isEmpty())) {
            MensajesErrores.advertencia("Nombre es necesario");
            return true;
        }
        if (getFestivoEntidad().getTipo() == null) {
            MensajesErrores.advertencia("Tipo es necesario");
            return true;
        }

        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            ejbFestivos.create(getFestivoEntidad(), parametrosSeguridad.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(FestivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        getFormulario().cancelar();
        buscar();
        return null;
    }

    public String grabar() {
        if (validar()) {
            return null;
        }
        try {
            ejbFestivos.edit(getFestivoEntidad(), parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(FestivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        getFormulario().cancelar();
        buscar();
        return null;
    }

    public String borrar() {
        try {
            ejbFestivos.remove(getFestivoEntidad(), parametrosSeguridad.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(FestivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        getFormulario().cancelar();
        buscar();
        return null;
    }

    /**
     * @return the festivos
     */
    public LazyDataModel<Festivos> getFestivos() {
        return festivos;
    }

    /**
     * @param festivos the festivos to set
     */
    public void setFestivos(LazyDataModel<Festivos> festivos) {
        this.festivos = festivos;
    }

    /**
     * @return the desde
     */
    public Date getDesde() {
        return desde;
    }

    /**
     * @param desde the desde to set
     */
    public void setDesde(Date desde) {
        this.desde = desde;
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
    public SelectItem[] getComboTipo() {
        int size = 4;
        SelectItem[] items = new SelectItem[size];
        items[0] = new SelectItem(null, "--- Seleccione Uno ---");
        items[1] = new SelectItem(0, "Día Festivo");
        items[2] = new SelectItem(1, "Sábado");
        items[3] = new SelectItem(2, "Domingo");
        return items;
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
     * @return the festivoEntidad
     */
    public Festivos getFestivoEntidad() {
        return festivoEntidad;
    }

    /**
     * @param festivoEntidad the festivoEntidad to set
     */
    public void setFestivoEntidad(Festivos festivoEntidad) {
        this.festivoEntidad = festivoEntidad;
    }
}
