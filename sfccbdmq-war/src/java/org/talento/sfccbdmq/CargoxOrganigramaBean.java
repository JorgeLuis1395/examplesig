/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

//import com.icesoft.faces.component.selectinputtext.SelectInputText;
import javax.faces.application.Resource;
import com.lowagie.text.DocumentException;
import org.contabilidad.sfccbdmq.MayorBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
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
import org.beans.sfccbdmq.ConceptosFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.OrganigramaFacade;
import org.entidades.sfccbdmq.Cargos;
import org.entidades.sfccbdmq.Cargosxorganigrama;
import org.entidades.sfccbdmq.Conceptos;
import org.entidades.sfccbdmq.Organigrama;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Tiposcontratos;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.icefaces.ace.event.TextChangeEvent;

/**
 *
 * @author edison
 */
@ManagedBean(name = "cargoxorganigrama")
@ViewScoped
public class CargoxOrganigramaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of CargoxOrganigramaBean
     */
    public CargoxOrganigramaBean() {
    }

    private Formulario formulario = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Organigrama organigrama;
    private List<Cargosxorganigrama> listaCarxOrg;
    private Cargosxorganigrama carXorg;
    private List<Cargos> listadoCargos;
    private String nomCargo;
    private Cargos cargoL;
    private List listadoOrganigramas;
    private Organigrama organigramaL;
    private Organigrama superior;
    private String nomOrganigrama;
    private String nomCargoB;
    private Cargosxorganigrama cargoRep;
    private Cargosxorganigrama cargoSup;
    private Cargosxorganigrama cargoCor;
    private String nomReporta;
    private String nomSupervisa;
    private String nomCordina;
    private List listaReporta;
    private List listaSupervisa;
    private List listaCordina;
    private List<Organigrama> listadoOrganigrama;
//    private List<Cargos> listadoCargos;
    private Tiposcontratos contrato;
    private Tiposcontratos contratoL;
    private Integer nombramiento;
    private boolean copiaConceptos = true;

    @EJB
    private CargosxorganigramaFacade ejbCarxOrg;
    @EJB
    private CargosFacade ejbCargos;
    @EJB
    private OrganigramaFacade ejbOrganigrama;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private ConceptosFacade ejbConceptos;
    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private Resource reporte;
    private String nombreArchivo;
    private String tipoArchivo;
    private String tipoMime;

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
        String nombreForma = "CargosxOrganigramaVista";
//        String empleadoString = (String) params.get("x");
//        if (empleadoString!=null){
//            return;
//        } else 
//        if (perfilString == null) {
//            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
//            parametrosSeguridad.cerraSession();
//        }
        this.setPerfil(parametrosSeguridad.traerPerfil(perfilString));
        if (this.getPerfil() == null) {
//            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
//            parametrosSeguridad.cerraSession();
            return;
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
            String where = "   o.activo=true and o.organigrama.activo=true ";

            //NOMBRE
            if (!((nomCargoB == null) || (nomCargoB.isEmpty()))) {
                where += " and upper(o.cargo.nombre) like :nombre";
                parametros.put("nombre", "%" + nomCargoB.toUpperCase() + "%");
            }
            //ORGANIGRAMA
            if (!((organigramaL == null))) {
                where += " and o.organigrama.codigo like :organigrama";
                parametros.put("organigrama", organigramaL.getCodigo() + "%");
            }

//            //CONTRATO
//            if (contratoL != null) {
//                where += " and o.tipocontrato=:contrato";
//                parametros.put("contrato", contratoL);
//            }
//            //NOMBRAMIENTO
//            if (nombramiento != null) {
//                if (nombramiento == 1) {
//                    where += " and o.tipocontrato.nombramiento=:nombramiento";
//                    parametros.put("nombramiento", true);
//                } else if (nombramiento == 2) {
//                    where += " and o.tipocontrato.nombramiento=:nombramiento";
//                    parametros.put("nombramiento", false);
//                }
//            }
            parametros.put(";where", where);
            parametros.put(";orden", "o.organigrama.codigo");
            listaCarxOrg = ejbCarxOrg.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(CargoxOrganigramaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String nuevo() {

        if (organigramaL == null) {
            MensajesErrores.advertencia("Seleccione un Organigrama");
            return null;
        }
        nomCordina = null;
        nomReporta = null;
        nomSupervisa = null;
        cargoRep = null;
        cargoSup = null;
        cargoCor = null;
        cargoL = null;
        nomCargo = new String();
        contrato = new Tiposcontratos();
        carXorg = new Cargosxorganigrama();
        carXorg.setOrganigrama(organigramaL);
        carXorg.setActivo(Boolean.TRUE);
        formulario.insertar();
        buscar();
        return null;
    }

    public String modificar() {

        nomCordina = null;
        nomReporta = null;
        nomSupervisa = null;
        cargoRep = null;
        cargoSup = null;
        cargoCor = null;

        carXorg = listaCarxOrg.get(formulario.getFila().getRowIndex());
        if (carXorg.getCoordina() != null) {
            cargoCor = carXorg.getCoordina();
            nomCordina = cargoCor.getCargo().getNombre();
        }
        if (carXorg.getReporta() != null) {
            cargoRep = carXorg.getReporta();
            superior = cargoRep.getOrganigrama();
            nomReporta = cargoRep.getCargo().getNombre();
        }
        if (carXorg.getSupervisa() != null) {
            cargoSup = carXorg.getSupervisa();
            nomSupervisa = cargoSup.getCargo().getNombre();

        }
        contrato = carXorg.getTipocontrato();
        cargoL = carXorg.getCargo();
        nomCargo = carXorg.getCargo().getNombre();
        organigramaL = carXorg.getOrganigrama();
        getComboOrganigramaSuperior();
        formulario.editar();
        return null;
    }

    private boolean validar() {
        if ((nomCargo == null || nomCargo.isEmpty())) {
            MensajesErrores.advertencia("Cargo es necesario");
            return true;
        }
        if (cargoL == null) {
            MensajesErrores.advertencia("Cargo es necesario");
            return true;
        }
        if ((carXorg.getPlazas() == null || carXorg.getPlazas() < 1)) {
            MensajesErrores.advertencia("Nro. de Plazas debe ser valor positivo");
            return true;
        }
        if (carXorg.getDescripcion() == null || carXorg.getDescripcion().isEmpty()) {
            MensajesErrores.advertencia("Descripción es necesaria");
            return true;
        }

//        if (carXorg.getTipocontrato() == null) {
//            MensajesErrores.advertencia("Tipo de contrato es necesario");
//            return true;
//        }
        // ver que no se repita
        if (formulario.isNuevo()) {
            if (cargoL == null) {
                MensajesErrores.advertencia("Puesto es necesario");
                return true;
            }
            Map parametros = new HashMap();
            parametros.put(";where", "o=:cargo=:cargo and o.organigrama=:organigrama and o.activo=true");
            parametros.put("cargo", cargoL);
            parametros.put("organigrama", organigramaL);
            try {
                int total = ejbCarxOrg.contar(parametros);
                if (total > 0) {
                    MensajesErrores.advertencia("Cargo ya presente en proceso");
                    return true;
                }
            } catch (ConsultarException ex) {
                Logger.getLogger(CargoxOrganigramaBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    public String insertar() {

        if (validar()) {
            return null;
        }
        try {
            carXorg.setReporta(cargoRep);
            carXorg.setSupervisa(cargoSup);
            carXorg.setCoordina(cargoCor);
            carXorg.setCargo(cargoL);
            //carXorg.setTipocontrato(contrato);
            ejbCarxOrg.create(carXorg, parametrosSeguridad.getLogueado().getUserid());
            // copiar conceptos

        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(CargoxOrganigramaBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        organigramaL = null;

        formulario.cancelar();
        buscar();
        return null;
    }

    public String grabar() {

        if (validar()) {
            return null;
        }
        try {
            carXorg.setReporta(cargoRep);
            carXorg.setSupervisa(cargoSup);
            carXorg.setCoordina(cargoCor);
            carXorg.setCargo(cargoL);
            //carXorg.setTipocontrato(contrato);
            ejbCarxOrg.edit(carXorg, parametrosSeguridad.getLogueado().getUserid());

        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(CargoxOrganigramaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
//        organigramaL=null;
        formulario.cancelar();
        buscar();
        return null;
    }

    public String eliminar() {
        nomCordina = null;
        nomReporta = null;
        nomSupervisa = null;
        cargoRep = null;
        cargoSup = null;
        cargoCor = null;

        carXorg = listaCarxOrg.get(formulario.getFila().getRowIndex());
        if (carXorg.getCoordina() != null) {
            cargoCor = carXorg.getCoordina();
            nomCordina = carXorg.getCoordina().getCargo().getNombre();
        }
        if (carXorg.getReporta() != null) {
            cargoRep = carXorg.getReporta();
            nomReporta = carXorg.getReporta().getCargo().getNombre();
        }
        if (carXorg.getSupervisa() != null) {
            cargoSup = carXorg.getSupervisa();
            nomSupervisa = carXorg.getSupervisa().getCargo().getNombre();
        }
        carXorg = listaCarxOrg.get(formulario.getFila().getRowIndex());
        organigramaL = carXorg.getOrganigrama();
        nomCargo = carXorg.getCargo().getNombre();
        contrato = carXorg.getTipocontrato();
        formulario.eliminar();
        return null;
    }

    public String borrar() {

        try {
//            carXorg.setActivo(Boolean.FALSE);
            // buscar si tiene empleados
            Map parametros = new HashMap();
            parametros.put(";where", "o.cargoactual=:cargo");
            parametros.put("cargo", carXorg);
            int total = ejbEmpleados.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("No es posible borrar existen empleados utilizando este cargo");
                return null;
            }
            parametros = new HashMap();
            parametros.put(";where", "o.reporta=:cargo");
            parametros.put("cargo", carXorg);
            total = ejbCarxOrg.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("No es posible  existen cargos que reportan a este");
                return null;
            }
            ejbCarxOrg.remove(carXorg, parametrosSeguridad.getLogueado().getUserid());
        } catch (BorrarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() );
            Logger.getLogger(CargoxOrganigramaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        organigramaL = null;
        return null;
    }

    public String cancelar() {
        formulario.cancelar();
        buscar();
        return null;
    }

//    public void cambiaCargos(ValueChangeEvent event) {
//        cargoL = null;
//        if (event.getComponent() instanceof SelectInputText) {
//            SelectInputText autoComplete
//                    = (SelectInputText) event.getComponent();
//            String newWord = (String) event.getNewValue();
//            List<Cargos> aux = new LinkedList<>();
//            // traer la consulta
//            Map parametros = new HashMap();
//            String where = " ";
//            where += "  upper(o.nombre) like:nombre and o.activo=true ";
//            parametros.put("nombre", newWord.toUpperCase() + "%");
//            parametros.put(";where", where);
//            parametros.put(";orden", " o.id desc");
//            int total = ((SelectInputText) event.getComponent()).getRows();
//            // Contadores
//            parametros.put(";inicial", 0);
//            parametros.put(";final", total);
//            try {
//                aux = ejbCargos.encontarParametros(parametros);
//            } catch (ConsultarException ex) {
//                MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
//                Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
//            }
//            listadoCargos = new ArrayList();
//            for (Cargos c : aux) {
//                //cargoL = c;
//                SelectItem s = new SelectItem(c, c.getNombre());
//                listadoCargos.add(s);
//            }
//            if (autoComplete.getSelectedItem() != null) {
//                cargoL = (Cargos) autoComplete.getSelectedItem().getValue();
//            } else {
//                Cargos tmp = null;
//                for (int i = 0, max = listadoCargos.size(); i < max; i++) {
//                    SelectItem e = (SelectItem) listadoCargos.get(i);
//                    if (e.getLabel().compareToIgnoreCase(newWord) == 0) {
//                        tmp = (Cargos) e.getValue();
//                    }
//                }
//                if (tmp != null) {
//                    cargoL = tmp;
//                }
//            }
//        }
//    }
    public SelectItem[] getComboOrganigrama() throws ConsultarException {
        Map parametros = new HashMap();
        parametros.put(";where", "o.activo=true");
        return Combos.getSelectItems(ejbOrganigrama.encontarParametros(parametros), true);
    }

    public SelectItem[] getComboCargos() throws ConsultarException {
        Map parametros = new HashMap();
        parametros.put(";where", "o.activo=true");
        return Combos.getSelectItems(ejbCargos.encontarParametros(parametros), true);
    }

    public void cambiaOrganigrama(ValueChangeEvent event) {
        organigramaL = null;
        String newWord = event.getNewValue() != null ? (String) event.getNewValue() : "";
        if (listadoOrganigrama == null) {
            return;
        }
        for (Organigrama o : listadoOrganigrama) {
            if (o.getNombre().compareToIgnoreCase(newWord) == 0) {
                organigramaL = o;
                return;
            }
        }
    }

    public void organigramaChangeEventHandler(TextChangeEvent event) {
        String newWord = event.getNewValue() != null ? (String) event.getNewValue() : "";
        if (newWord.isEmpty()) {
            organigramaL = null;
        }
        Map parametros = new HashMap();
        String where = "";
        where += "  upper(o.nombre) like :nombre and o.activo=true ";
        parametros.put("nombre", newWord.toUpperCase() + "%");
        parametros.put(";where", where);
        parametros.put(";orden", " o.nombre desc");
        int total = 15;
        // Contadores
        parametros.put(";inicial", 0);
        parametros.put(";final", total);
        try {
            listadoOrganigrama = ejbOrganigrama.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }
    }

    public Cargosxorganigrama traer(Integer id) throws ConsultarException {
        return ejbCarxOrg.find(id);
    }

    public SelectItem[] getComboCargosOrganigrama() {
        if (organigramaL == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.activo=true and o.organigrama=:organigrama");
        parametros.put("organigrama", organigramaL);
        try {
            return Combos.getSelectItems2(ejbCarxOrg.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            Logger.getLogger(CargoxOrganigramaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Integer getcontadorVacantes(Cargosxorganigrama cargo) {
        if (cargo == null) {
            return null;
        }
        Integer nroPlazas = cargo.getPlazas();
        Integer nroOcupadas = 0;
        Integer nroVacantes = 0;
        try {

            Map parametros = new HashMap();
            parametros.put(";where", "o.activo=true and o.cargoactual=:cargo and o.fechasalida is null");
//            parametros.put(";where", "o.activo=true and o.cargoactual=:cargo and o.tipocontrato.nombramiento=true");
            parametros.put("cargo", cargo);
            nroOcupadas = ejbEmpleados.contar(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(CargoxOrganigramaBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        nroVacantes = nroPlazas - nroOcupadas;
        return (nroVacantes);
    }

//    public void cambiaCargosRep(ValueChangeEvent event) {
//        cargoRep = null;
//        if (event.getComponent() instanceof SelectInputText) {
//            SelectInputText autoComplete
//                    = (SelectInputText) event.getComponent();
//            String newWord = (String) event.getNewValue();
//            List<Cargosxorganigrama> aux = new LinkedList<>();
//            // traer la consulta
//            Map parametros = new HashMap();
//            String where = " ";
//            where += "  upper(o.cargo.nombre) like:nombre and o.activo=true  and o.organigrama=:organigrama";
//            parametros.put("nombre", newWord.toUpperCase() + "%");
//            parametros.put("organigrama", organigramaL);
//            parametros.put(";where", where);
//            parametros.put(";orden", " o.id desc");
//            int total = ((SelectInputText) event.getComponent()).getRows();
//            // Contadores
//            parametros.put(";inicial", 0);
//            parametros.put(";final", total);
//            try {
//                aux = ejbCarxOrg.encontarParametros(parametros);
//            } catch (ConsultarException ex) {
//                MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
//                Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
//            }
//            listaReporta = new ArrayList();
//            for (Cargosxorganigrama c : aux) {
//                //cargoRep = c;
//                SelectItem s = new SelectItem(c, c.getCargo().getNombre());
//                listaReporta.add(s);
//            }
//            if (autoComplete.getSelectedItem() != null) {
//                cargoRep = (Cargosxorganigrama) autoComplete.getSelectedItem().getValue();
//            } else {
//                Cargosxorganigrama tmp = null;
//                for (int i = 0, max = listaReporta.size(); i < max; i++) {
//                    SelectItem e = (SelectItem) listaReporta.get(i);
//                    if (e.getLabel().compareToIgnoreCase(newWord) == 0) {
//                        tmp = (Cargosxorganigrama) e.getValue();
//                    }
//                }
//                if (tmp != null) {
//                    cargoRep = tmp;
//                }
//            }
//        }
//    }
//
//    public void cambiaCargosSup(ValueChangeEvent event) {
//        cargoSup = null;
//        if (event.getComponent() instanceof SelectInputText) {
//            SelectInputText autoComplete
//                    = (SelectInputText) event.getComponent();
//            String newWord = (String) event.getNewValue();
//            List<Cargosxorganigrama> aux = new LinkedList<>();
//            // traer la consulta
//            Map parametros = new HashMap();
//            String where = " ";
//            where += "  upper(o.cargo.nombre) like:nombre and o.activo=true  and o.organigrama=:organigrama";
//            parametros.put("nombre", newWord.toUpperCase() + "%");
//            parametros.put("organigrama", organigramaL);
//            parametros.put(";where", where);
//            parametros.put(";orden", " o.id desc");
//            int total = ((SelectInputText) event.getComponent()).getRows();
//            // Contadores
//            parametros.put(";inicial", 0);
//            parametros.put(";final", total);
//            try {
//                aux = ejbCarxOrg.encontarParametros(parametros);
//            } catch (ConsultarException ex) {
//                MensajesErrores.error(ex.getMessage());
//                Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
//            }
//            listaSupervisa = new ArrayList();
//            for (Cargosxorganigrama c : aux) {
//                //cargoSup = c;
//                SelectItem s = new SelectItem(c, c.getCargo().getNombre());
//                listaSupervisa.add(s);
//            }
//            if (autoComplete.getSelectedItem() != null) {
//                cargoSup = (Cargosxorganigrama) autoComplete.getSelectedItem().getValue();
//            } else {
//                Cargosxorganigrama tmp = null;
//                for (int i = 0, max = listaSupervisa.size(); i < max; i++) {
//                    SelectItem e = (SelectItem) listaSupervisa.get(i);
//                    if (e.getLabel().compareToIgnoreCase(newWord) == 0) {
//                        tmp = (Cargosxorganigrama) e.getValue();
//                    }
//                }
//                if (tmp != null) {
//                    cargoSup = tmp;
//                }
//            }
//        }
//    }
//
//    public void cambiaCargosCor(ValueChangeEvent event) {
//        cargoCor = null;
//        if (event.getComponent() instanceof SelectInputText) {
//            SelectInputText autoComplete
//                    = (SelectInputText) event.getComponent();
//            String newWord = (String) event.getNewValue();
//            List<Cargosxorganigrama> aux = new LinkedList<>();
//            // traer la consulta
//            Map parametros = new HashMap();
//            String where = " ";
//            where += "  upper(o.cargo.nombre) like:nombre and o.activo=true  and o.organigrama=:organigrama";
//            parametros.put("nombre", newWord.toUpperCase() + "%");
//            parametros.put("organigrama", organigramaL);
//            parametros.put(";where", where);
//            parametros.put(";orden", " o.id desc");
//            int total = ((SelectInputText) event.getComponent()).getRows();
//            // Contadores
//            parametros.put(";inicial", 0);
//            parametros.put(";final", total);
//            try {
//                aux = ejbCarxOrg.encontarParametros(parametros);
//            } catch (ConsultarException ex) {
//                MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
//                Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
//            }
//            listaCordina = new ArrayList();
//            for (Cargosxorganigrama c : aux) {
//                //cargoCor = c;
//                SelectItem s = new SelectItem(c, c.getCargo().getNombre());
//                listaCordina.add(s);
//            }
//            if (autoComplete.getSelectedItem() != null) {
//                cargoCor = (Cargosxorganigrama) autoComplete.getSelectedItem().getValue();
//            } else {
//                Cargosxorganigrama tmp = null;
//                for (int i = 0, max = listaCordina.size(); i < max; i++) {
//                    SelectItem e = (SelectItem) listaCordina.get(i);
//                    if (e.getLabel().compareToIgnoreCase(newWord) == 0) {
//                        tmp = (Cargosxorganigrama) e.getValue();
//                    }
//                }
//                if (tmp != null) {
//                    cargoCor = tmp;
//                }
//            }
//        }
//    }
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
     * @return the listaCarxOrg
     */
    public List<Cargosxorganigrama> getListaCarxOrg() {
        return listaCarxOrg;
    }

    /**
     * @param listaCarxOrg the listaCarxOrg to set
     */
    public void setListaCarxOrg(List<Cargosxorganigrama> listaCarxOrg) {
        this.listaCarxOrg = listaCarxOrg;
    }

    /**
     * @return the carXorg
     */
    public Cargosxorganigrama getCarXorg() {
        return carXorg;
    }

    /**
     * @param carXorg the carXorg to set
     */
    public void setCarXorg(Cargosxorganigrama carXorg) {
        this.carXorg = carXorg;
    }

    /**
     * @return the listadoCargos
     */
    public List<Cargos> getListadoCargos() {
        return listadoCargos;
    }

    /**
     * @param listadoCargos the listadoCargos to set
     */
    public void setListadoCargos(List<Cargos> listadoCargos) {
        this.listadoCargos = listadoCargos;
    }

    /**
     * @return the nomCargo
     */
    public String getNomCargo() {
        return nomCargo;
    }

    /**
     * @param nomCargo the nomCargo to set
     */
    public void setNomCargo(String nomCargo) {
        this.nomCargo = nomCargo;
    }

    /**
     * @return the cargoL
     */
    public Cargos getCargoL() {
        return cargoL;
    }

    /**
     * @param cargoL the cargoL to set
     */
    public void setCargoL(Cargos cargoL) {
        this.cargoL = cargoL;
    }

    /**
     * @return the listadoOrganigramas
     */
    public List getListadoOrganigramas() {
        return listadoOrganigramas;
    }

    /**
     * @param listadoOrganigramas the listadoOrganigramas to set
     */
    public void setListadoOrganigramas(List listadoOrganigramas) {
        this.listadoOrganigramas = listadoOrganigramas;
    }

    /**
     * @return the organigramaL
     */
    public Organigrama getOrganigramaL() {
        return organigramaL;
    }

    /**
     * @param organigramaL the organigramaL to set
     */
    public void setOrganigramaL(Organigrama organigramaL) {
        this.organigramaL = organigramaL;
    }

    /**
     * @return the nomOrganigrama
     */
    public String getNomOrganigrama() {
        return nomOrganigrama;
    }

    /**
     * @param nomOrganigrama the nomOrganigrama to set
     */
    public void setNomOrganigrama(String nomOrganigrama) {
        this.nomOrganigrama = nomOrganigrama;
    }

    /**
     * @return the nomCargoB
     */
    public String getNomCargoB() {
        return nomCargoB;
    }

    /**
     * @param nomCargoB the nomCargoB to set
     */
    public void setNomCargoB(String nomCargoB) {
        this.nomCargoB = nomCargoB;
    }

    /**
     * @return the cargoRep
     */
    public Cargosxorganigrama getCargoRep() {
        return cargoRep;
    }

    /**
     * @param cargoRep the cargoRep to set
     */
    public void setCargoRep(Cargosxorganigrama cargoRep) {
        this.cargoRep = cargoRep;
    }

    /**
     * @return the cargoSup
     */
    public Cargosxorganigrama getCargoSup() {
        return cargoSup;
    }

    /**
     * @param cargoSup the cargoSup to set
     */
    public void setCargoSup(Cargosxorganigrama cargoSup) {
        this.cargoSup = cargoSup;
    }

    /**
     * @return the cargoCor
     */
    public Cargosxorganigrama getCargoCor() {
        return cargoCor;
    }

    /**
     * @param cargoCor the cargoCor to set
     */
    public void setCargoCor(Cargosxorganigrama cargoCor) {
        this.cargoCor = cargoCor;
    }

    /**
     * @return the nomReporta
     */
    public String getNomReporta() {
        return nomReporta;
    }

    /**
     * @param nomReporta the nomReporta to set
     */
    public void setNomReporta(String nomReporta) {
        this.nomReporta = nomReporta;
    }

    /**
     * @return the nomSupervisa
     */
    public String getNomSupervisa() {
        return nomSupervisa;
    }

    /**
     * @param nomSupervisa the nomSupervisa to set
     */
    public void setNomSupervisa(String nomSupervisa) {
        this.nomSupervisa = nomSupervisa;
    }

    /**
     * @return the nomCordina
     */
    public String getNomCordina() {
        return nomCordina;
    }

    /**
     * @param nomCordina the nomCordina to set
     */
    public void setNomCordina(String nomCordina) {
        this.nomCordina = nomCordina;
    }

    /**
     * @return the listaReporta
     */
    public List getListaReporta() {
        return listaReporta;
    }

    /**
     * @param listaReporta the listaReporta to set
     */
    public void setListaReporta(List listaReporta) {
        this.listaReporta = listaReporta;
    }

    /**
     * @return the listaSupervisa
     */
    public List getListaSupervisa() {
        return listaSupervisa;
    }

    /**
     * @param listaSupervisa the listaSupervisa to set
     */
    public void setListaSupervisa(List listaSupervisa) {
        this.listaSupervisa = listaSupervisa;
    }

    /**
     * @return the listaCordina
     */
    public List getListaCordina() {
        return listaCordina;
    }

    /**
     * @param listaCordina the listaCordina to set
     */
    public void setListaCordina(List listaCordina) {
        this.listaCordina = listaCordina;
    }

    /**
     * @return the contrato
     */
    public Tiposcontratos getContrato() {
        return contrato;
    }

    /**
     * @param contrato the contrato to set
     */
    public void setContrato(Tiposcontratos contrato) {
        this.contrato = contrato;
    }

    /**
     * @return the contratoL
     */
    public Tiposcontratos getContratoL() {
        return contratoL;
    }

    /**
     * @param contratoL the contratoL to set
     */
    public void setContratoL(Tiposcontratos contratoL) {
        this.contratoL = contratoL;
    }

    /**
     * @return the nombramiento
     */
    public Integer getNombramiento() {
        return nombramiento;
    }

    /**
     * @param nombramiento the nombramiento to set
     */
    public void setNombramiento(Integer nombramiento) {
        this.nombramiento = nombramiento;
    }

    public SelectItem[] getComboCargosSuperior() {
        if (superior == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.activo=true and o.organigrama=:organigrama");
        parametros.put("organigrama", superior);
        try {
            return Combos.getSelectItems(ejbCarxOrg.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            Logger.getLogger(CargoxOrganigramaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboOrganigramaSuperior() {
        if (carXorg == null) {
            return null;
        }
        if (carXorg.getOrganigrama() == null) {
            return null;
        }

        List<Organigrama> listOrganigrama = new LinkedList<>();
        if (cargoRep != null) {
            listOrganigrama.add(cargoRep.getOrganigrama());
        }
        Organigrama o = carXorg.getOrganigrama();
        listOrganigrama.add(o);
        while (o.getSuperior() != null) {
            listOrganigrama.add(o.getSuperior());
            o = o.getSuperior();
        }

//        if (organigramaL.getSuperior() != null) {
//            listOrganigrama.add(organigramaL.getSuperior());
//        }
//        listOrganigrama.add(organigramaL);
        return Combos.getSelectItems(listOrganigrama, true);

    }

    /**
     * @return the superior
     */
    public Organigrama getSuperior() {
        return superior;
    }

    /**
     * @param superior the superior to set
     */
    public void setSuperior(Organigrama superior) {
        this.superior = superior;
    }

    /**
     * @return the copiaConceptos
     */
    public boolean isCopiaConceptos() {
        return copiaConceptos;
    }

    /**
     * @param copiaConceptos the copiaConceptos to set
     */
    public void setCopiaConceptos(boolean copiaConceptos) {
        this.copiaConceptos = copiaConceptos;
    }

    /**
     * @return the formularioImprimir
     */
    public Formulario getFormularioImprimir() {
        return formularioImprimir;
    }

    /**
     * @param formularioImprimir the formularioImprimir to set
     */
    public void setFormularioImprimir(Formulario formularioImprimir) {
        this.formularioImprimir = formularioImprimir;
    }

    /**
     * @return the reporte
     */
    public Resource getReporte() {
        return reporte;
    }

    /**
     * @param reporte the reporte to set
     */
    public void setReporte(Resource reporte) {
        this.reporte = reporte;
    }

    /**
     * @return the nombreArchivo
     */
    public String getNombreArchivo() {
        return nombreArchivo;
    }

    /**
     * @param nombreArchivo the nombreArchivo to set
     */
    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    /**
     * @return the tipoArchivo
     */
    public String getTipoArchivo() {
        return tipoArchivo;
    }

    /**
     * @param tipoArchivo the tipoArchivo to set
     */
    public void setTipoArchivo(String tipoArchivo) {
        this.tipoArchivo = tipoArchivo;
    }

    /**
     * @return the tipoMime
     */
    public String getTipoMime() {
        return tipoMime;
    }

    /**
     * @param tipoMime the tipoMime to set
     */
    public void setTipoMime(String tipoMime) {
        this.tipoMime = tipoMime;
    }

    public String imprimir() {
        try {
            DocumentoPDF pdf = new DocumentoPDF("Puestos por Organigrama\n", parametrosSeguridad.getLogueado().getUserid());
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Código Organigrama"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Código Puesto"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre Puesto"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Plazas"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Vacantes"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Reporta a"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Director de Proceso"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Descripción"));
            List<AuxiliarReporte> columnas = new LinkedList<>();
            for (Cargosxorganigrama cxo : listaCarxOrg) {
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cxo.getOrganigrama().getCodigo()));
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, cxo.getOrganigrama().getNombre()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cxo.getCargo().getCodigo()));
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, cxo.getCargo().getNombre()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cxo.getPlazas() == null ? "" : cxo.getPlazas().toString()));
                Integer vacantes = getcontadorVacantes(cxo);
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, vacantes == null ? "" : cxo.getPlazas().toString()));
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, cxo.getReporta() == null ? "" : cxo.getReporta().getCargo().getNombre()));
                if (cxo.getJefeproceso()) {
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "SI"));
                } else {
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "NO"));
                }
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, cxo.getDescripcion()));
            }
            pdf.agregarTablaReporte(titulos, columnas, 9, 100, null);
            reporte = pdf.traerRecurso();
            nombreArchivo = "CargosxOrg.pdf";
            tipoArchivo = "Exportar a PDF";
            tipoMime = "application/pdf";
            formularioImprimir.insertar();

        } catch (IOException | DocumentException ex) {
            Logger.getLogger(CargoxOrganigramaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String exportar() {

        try {
            // 
            DocumentoXLS xls = new DocumentoXLS("Puessto x Organigrama");
            List<AuxiliarReporte> campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", "Código Organigrama"));
            campos.add(new AuxiliarReporte("String", "Nombre"));
            campos.add(new AuxiliarReporte("String", "Código Puesto"));
            campos.add(new AuxiliarReporte("String", "Nombre Puesto"));
            campos.add(new AuxiliarReporte("String", "Plazas"));
            campos.add(new AuxiliarReporte("String", "Vacantes"));
            campos.add(new AuxiliarReporte("String", "Reporta a"));
            campos.add(new AuxiliarReporte("String", "Director de Proceso"));
            campos.add(new AuxiliarReporte("String", "Descripción"));
            xls.agregarFila(campos, true);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.ENGLISH));
            for (Cargosxorganigrama cxo : listaCarxOrg) {
                campos = new LinkedList<>();
                campos.add(new AuxiliarReporte("String", cxo.getOrganigrama().getCodigo()));
                campos.add(new AuxiliarReporte("String", cxo.getOrganigrama().getNombre()));
                campos.add(new AuxiliarReporte("String", cxo.getCargo().getCodigo()));
                campos.add(new AuxiliarReporte("String", cxo.getCargo().getNombre()));
                campos.add(new AuxiliarReporte("String", cxo.getPlazas() == null ? "" : cxo.getPlazas().toString()));
                Integer vacantes = getcontadorVacantes(cxo);
                campos.add(new AuxiliarReporte("String", vacantes == null ? "" : cxo.getPlazas().toString()));
                campos.add(new AuxiliarReporte("String", cxo.getReporta() == null ? "" : cxo.getReporta().getCargo().getNombre()));
                if (cxo.getJefeproceso()) {
                    campos.add(new AuxiliarReporte("String", "SI"));
                } else {
                    campos.add(new AuxiliarReporte("String", "NO"));
                }
                campos.add(new AuxiliarReporte("String", cxo.getDescripcion()));
                xls.agregarFila(campos, false);
            }
            nombreArchivo = "PuesxOrg.xls";
            tipoArchivo = "Exportar a XLS";
            tipoMime = "application/xls";
            reporte = xls.traerRecurso();
            formularioImprimir.insertar();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(MayorBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * @return the listadoOrganigrama
     */
    public List<Organigrama> getListadoOrganigrama() {
        return listadoOrganigrama;
    }

    /**
     * @param listadoOrganigrama the listadoOrganigrama to set
     */
    public void setListadoOrganigrama(List<Organigrama> listadoOrganigrama) {
        this.listadoOrganigrama = listadoOrganigrama;
    }

    public void cambiaCargo(ValueChangeEvent event) {
        cargoL = null;
        String newWord = event.getNewValue() != null ? (String) event.getNewValue() : "";
        if (listadoCargos == null) {
            return;
        }
        for (Cargos c : listadoCargos) {
            if (c.getNombre().compareToIgnoreCase(newWord) == 0) {
                cargoL = c;
                return;
            }
        }
    }

    public void cargoChangeEventHandler(TextChangeEvent event) {
        String newWord = event.getNewValue() != null ? (String) event.getNewValue() : "";
        Map parametros = new HashMap();
        String where = "";
        where += "  upper(o.nombre) like :nombre";
        parametros.put("nombre", newWord.toUpperCase() + "%");
        parametros.put(";where", where);
        parametros.put(";orden", " o.nombre desc");
        int total = 15;
        // Contadores
        parametros.put(";inicial", 0);
        parametros.put(";final", total);
        try {
            listadoCargos = ejbCargos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }
    }
}
