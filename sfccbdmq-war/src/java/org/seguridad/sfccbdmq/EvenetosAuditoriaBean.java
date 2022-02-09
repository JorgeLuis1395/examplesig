/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.seguridad.sfccbdmq;

//import com.logs.entidades.Eventos;
//import com.logs.servicios.EventosFacade;
import com.ejb.sfcarchivos.EventosFacade;
import com.entidades.sfcarchivos.Eventos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
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
import org.entidades.sfccbdmq.Perfiles;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "auditoriaSfccbdmq")
@ViewScoped
public class EvenetosAuditoriaBean implements Serializable {

    private Formulario formulario = new Formulario();
    // Lazzy para el menu de lo que esta abierto
    private LazyDataModel<Eventos> eventos;
    private String objeto;
    private String beanBuscar;
    private Date desde;
    private Date hasta;
    @EJB
    private EventosFacade ejbEventos;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
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
     * Creates a new instance of FuncionesBean
     */
    public EvenetosAuditoriaBean() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DATE, 1);
        c.set(Calendar.MONTH, 0);
        desde = new Date();
        hasta = new Date();
        eventos = new LazyDataModel<Eventos>() {
            @Override
            public List<Eventos> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return cargar(i, pageSize, scs, map);
            }
        };

    }

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "EventosVista";
        if (perfil == null) {
            MensajesErrores.fatal("Sin perfil válido");
            getSeguridadbean().cerraSession();
        }
        this.setPerfil(getSeguridadbean().traerPerfil(perfil));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil válido");
            getSeguridadbean().cerraSession();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(seguridadbean.getGrpUsuario().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                getSeguridadbean().cerraSession();
//            }
//        }
    }

    public List<Eventos> cargar(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
        Map parametros = new HashMap();
        if (scs.length == 0) {
            parametros.put(";orden", "o.fecha desc,o.hora desc");
        } else {
            parametros.put(";orden", "o." + scs[0].getPropertyName()
                    + (scs[0].isAscending() ? " ASC" : " DESC"));
        }
        String where = "o.fecha between :desde and :hasta";
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);
        for (Map.Entry e : map.entrySet()) {
            String clave = (String) e.getKey();

            if (clave.equals("id")) {
                Integer valor = Integer.valueOf((String) e.getValue());
                where += " and o.id=:id ";
                parametros.put("id", valor);
            } else {
                String valor = (String) e.getValue();
                where += " and upper(o." + clave + ") like :" + clave;
                parametros.put(clave, valor.toUpperCase() + "%");
            }
        }
        if (beanBuscar != null) {
            where += " and upper(o.bean) like :bean";
            parametros.put("bean", beanBuscar.toUpperCase() + "%");
        }
        if (!((objeto == null) || (objeto.trim().isEmpty()))) {
            where += " and upper(o.objeto) like :objeto";
            parametros.put("objeto", "%" + objeto.toUpperCase() + "%");
        }
        parametros.put(";where", where);
        int total = 0;
        try {
            total = ejbEventos.contar(parametros);
        } catch (Exception ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(EvenetosAuditoriaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        int endIndex = i + pageSize;
        if (endIndex > total) {
            endIndex = total;
        }

        parametros.put(";inicial", i);
        parametros.put(";final", endIndex);
        getEventos().setRowCount(total);
        try {
            return ejbEventos.encontarParametros(parametros);
        } catch (Exception ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(EvenetosAuditoriaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String buscar() {

        setEventos(new LazyDataModel<Eventos>() {
            @Override
            public List<Eventos> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return cargar(i, pageSize, scs, map);
            }
        });
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
     * @return the objeto
     */
    public String getObjeto() {
        return objeto;
    }

    /**
     * @param objeto the objeto to set
     */
    public void setObjeto(String objeto) {
        this.objeto = objeto;
    }

    /**
     * @return the bean
     */
    public String getBeanBuscar() {
        return beanBuscar;
    }

    /**
     * @param bean the bean to set
     */
    public void setBeanBuscar(String beanBuscar) {
        this.beanBuscar = beanBuscar;
    }

//    /**
//     * @return the eventos
//     */
//    public LazyDataModel<Eventos> getEventos() {
//        return eventos;
//    }
//
//    /**
//     * @param eventos the eventos to set
//     */
//    public void setEventos(LazyDataModel<Eventos> eventos) {
//        this.eventos = eventos;
//    }

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
     * @return the eventos
     */
    public LazyDataModel<Eventos> getEventos() {
        return eventos;
    }

    /**
     * @param eventos the eventos to set
     */
    public void setEventos(LazyDataModel<Eventos> eventos) {
        this.eventos = eventos;
    }
}
