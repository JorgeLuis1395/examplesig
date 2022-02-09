/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

//import com.icesoft.faces.component.selectinputtext.SelectInputText;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
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
import org.beans.sfccbdmq.CargosFacade;
import org.beans.sfccbdmq.CargosxorganigramaFacade;
import org.beans.sfccbdmq.EscalassalarialesFacade;
import org.beans.sfccbdmq.NivelesgestionFacade;
import org.beans.sfccbdmq.OrganigramaFacade;
import org.beans.sfccbdmq.SolicitudescargoFacade;
import org.entidades.sfccbdmq.Cargosxorganigrama;
import org.entidades.sfccbdmq.Escalassalariales;
import org.entidades.sfccbdmq.Nivelesgestion;
import org.entidades.sfccbdmq.Organigrama;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Solicitudescargo;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "escojerSolicitud")
@ViewScoped
public class EscojerSolicitudBean {

    private List<Solicitudescargo> solicitudes;
    private String cedulapostulante;
    private List<Escalassalariales> listaEscalas;
    private String codigocargo;
    private Nivelesgestion nivelB;
    private Escalassalariales salarioB;
    private Organigrama organigrama;
    private String nomCargoxOrg;
    private List listaCarxOrg;
    private Cargosxorganigrama cargo;
    private List<Nivelesgestion> listaNiveles;
    private Formulario formulario = new Formulario();
    @EJB
    private NivelesgestionFacade ejbNiveles;
    @EJB
    private EscalassalarialesFacade ejbEscalas;
    @EJB
    private SolicitudescargoFacade ejbSolicitudCargo;
    @EJB
    private CargosFacade ejbCargos;
    @EJB
    private OrganigramaFacade ejbOrganigrama;
    @EJB
    private CargosxorganigramaFacade ejbCargosOrganigrama;
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
     * Creates a new instance of EscojerSolicitudBean
     */
    public EscojerSolicitudBean() {
    }

    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;

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
        String nombreForma = "OficinasVista";
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

    public String buscar() {

        

        try {
            Map parametros = new HashMap();
            String where = "o.activo=true and o.vigente=true";

            
            if (getCodigocargo() != null && !getCodigocargo().isEmpty()) {
                where += " and upper(o.cargovacante.cargo.codigo)like:cod";
                parametros.put("cod", getCodigocargo().toUpperCase() + "%");
            }
            if (getNivelB() != null) {
                where += " and o.cargovacante.cargo.nivel=:nil";
                parametros.put("nil", getNivelB());
            }
            if (getSalarioB() != null) {
                where += " and o.cargovacante.cargo.escalasalarial=:esc";
                parametros.put("esc", getSalarioB());
            }
            if (getCargo() != null) {
                where += " and o.cargovacante=:cargo";
                parametros.put("cargo", getCargo());
            }

            parametros.put(";where", where);
            setSolicitudes(ejbSolicitudCargo.encontarParametros(parametros));
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(SolicitudesCargoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String solicitudesenviar(Solicitudescargo item) {
//        if(getCedulapostulante().isEmpty() || getCedulapostulante()==null){
//            MensajesErrores.advertencia("Escriba la cédula del postulante");
//           return null;
//        }
        parametrosSeguridad.setSolicitudcargo(item);
//        getSeguridadbean().setCedulapostulante(cedulapostulante);
        return "PostulacionesCargoVista.jsf?faces-redirect=true";
    }

    public String cancelar() {
        return "BlancoVista.jsf?faces-redirect=true";
    }

    public SelectItem[] getComboNivelesGestion() {
        
        try {
            Map parametros = new HashMap();
            String where = " o.activo=true";
            parametros.put(";where", where);
            setListaNiveles(ejbNiveles.encontarParametros(parametros));
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(NivelesGestionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Combos.getSelectItems(getListaNiveles(), true);
    }

    public SelectItem[] getComboEscalas() {
       
        try {
            Map parametros = new HashMap();
            String where = " o.activo=true";
            parametros.put(";where", where);
            parametros.put(";orden", "o.nombre asc");
            setListaEscalas(ejbEscalas.encontarParametros(parametros));
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(EscalaSalarialBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Combos.getSelectItems(getListaEscalas(), true);
    }

    public SelectItem[] getComboOrganigrama() {

        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.activo=true ");
            parametros.put(";orden", "o.nombre asc");
            return Combos.getSelectItems(ejbOrganigrama.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            Logger.getLogger(SolicitudesCargoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboCargos() {
        try {
            if (organigrama == null) {
                return null;
            }
            Map parametros = new HashMap();
            parametros.put(";where", "o.activo=true and o.organigrama=:organigrama");
            parametros.put(";orden", "o.cargo.nombre asc");
            parametros.put("organigrama", organigrama);
            return Combos.getSelectItems(ejbCargosOrganigrama.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            Logger.getLogger(SolicitudesCargoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void cambiaOrganigrama(ValueChangeEvent event) {
        organigrama = (Organigrama) event.getNewValue();
    }

//    public void cambiaCargosxOrganigrama(ValueChangeEvent event) {
//        setCargo(null);
//        if (event.getComponent() instanceof SelectInputText) {
//            SelectInputText autoComplete
//                    = (SelectInputText) event.getComponent();
//            String newWord = (String) event.getNewValue();
//            List<Cargosxorganigrama> aux = new LinkedList<>();
//            // traer la consulta
//            Map parametros = new HashMap();
//            String where = " ";
//            where += "  upper(o.cargo.nombre) like:nombre and o.activo=true ";
//            parametros.put("nombre", newWord.toUpperCase() + "%");
//            parametros.put(";where", where);
//            parametros.put(";orden", " o.id desc");
//            int total = ((SelectInputText) event.getComponent()).getRows();
//            // Contadores
//            parametros.put(";inicial", 0);
//            parametros.put(";final", total);
//            try {
//                aux = ejbCargosOrganigrama.encontarParametros(parametros);
//            } catch (ConsultarException ex) {
//                MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
//                Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
//            }
//            setListaCarxOrg(new ArrayList());
//            for (Cargosxorganigrama c : aux) {
//                //cargoCor = c;
//                SelectItem s = new SelectItem(c, c.getCargo().getNombre());
//                getListaCarxOrg().add(s);
//            }
//            if (autoComplete.getSelectedItem() != null) {
//                setCargo((Cargosxorganigrama) autoComplete.getSelectedItem().getValue());
//            } else {
//                Cargosxorganigrama tmp = null;
//                for (int i = 0, max = getListaCarxOrg().size(); i < max; i++) {
//                    SelectItem e = (SelectItem) getListaCarxOrg().get(i);
//                    if (e.getLabel().compareToIgnoreCase(newWord) == 0) {
//                        tmp = (Cargosxorganigrama) e.getValue();
//                    }
//                }
//                if (tmp != null) {
//                    setCargo(tmp);
//                }
//            }
//        }
//    }


    /**
     * @return the solicitudes
     */
    public List<Solicitudescargo> getSolicitudes() {
        return solicitudes;
    }

    /**
     * @param solicitudes the solicitudes to set
     */
    public void setSolicitudes(List<Solicitudescargo> solicitudes) {
        this.solicitudes = solicitudes;
    }

    /**
     * @return the cedulapostulante
     */
    public String getCedulapostulante() {
        return cedulapostulante;
    }

    /**
     * @param cedulapostulante the cedulapostulante to set
     */
    public void setCedulapostulante(String cedulapostulante) {
        this.cedulapostulante = cedulapostulante;
    }

    /**
     * @return the codigocargo
     */
    public String getCodigocargo() {
        return codigocargo;
    }

    /**
     * @param codigocargo the codigocargo to set
     */
    public void setCodigocargo(String codigocargo) {
        this.codigocargo = codigocargo;
    }

    /**
     * @return the nivelB
     */
    public Nivelesgestion getNivelB() {
        return nivelB;
    }

    /**
     * @param nivelB the nivelB to set
     */
    public void setNivelB(Nivelesgestion nivelB) {
        this.nivelB = nivelB;
    }

    /**
     * @return the salarioB
     */
    public Escalassalariales getSalarioB() {
        return salarioB;
    }

    /**
     * @param salarioB the salarioB to set
     */
    public void setSalarioB(Escalassalariales salarioB) {
        this.salarioB = salarioB;
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
     * @return the cargo
     */
    public Cargosxorganigrama getCargo() {
        return cargo;
    }

    /**
     * @param cargo the cargo to set
     */
    public void setCargo(Cargosxorganigrama cargo) {
        this.cargo = cargo;
    }

    /**
     * @return the listaNiveles
     */
    public List<Nivelesgestion> getListaNiveles() {
        return listaNiveles;
    }

    /**
     * @param listaNiveles the listaNiveles to set
     */
    public void setListaNiveles(List<Nivelesgestion> listaNiveles) {
        this.listaNiveles = listaNiveles;
    }

    /**
     * @return the listaEscalas
     */
    public List<Escalassalariales> getListaEscalas() {
        return listaEscalas;
    }

    /**
     * @param listaEscalas the listaEscalas to set
     */
    public void setListaEscalas(List<Escalassalariales> listaEscalas) {
        this.listaEscalas = listaEscalas;
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
     * @return the nomCargoxOrg
     */
    public String getNomCargoxOrg() {
        return nomCargoxOrg;
    }

    /**
     * @param nomCargoxOrg the nomCargoxOrg to set
     */
    public void setNomCargoxOrg(String nomCargoxOrg) {
        this.nomCargoxOrg = nomCargoxOrg;
    }

    /**
     * @return the listaCarxOrg
     */
    public List getListaCarxOrg() {
        return listaCarxOrg;
    }

    /**
     * @param listaCarxOrg the listaCarxOrg to set
     */
    public void setListaCarxOrg(List listaCarxOrg) {
        this.listaCarxOrg = listaCarxOrg;
    }

}
