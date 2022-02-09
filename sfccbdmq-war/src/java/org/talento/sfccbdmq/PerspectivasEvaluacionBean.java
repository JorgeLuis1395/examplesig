/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import javax.faces.model.SelectItem;
import org.beans.sfccbdmq.CargosxorganigramaFacade;
import org.beans.sfccbdmq.CodigosFacade;
import org.beans.sfccbdmq.InformantesFacade;
import org.beans.sfccbdmq.PerspectivasFacade;
import org.entidades.sfccbdmq.Cargosxorganigrama;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Informantes;
import org.entidades.sfccbdmq.Organigrama;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Perspectivas;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author luisfernando
 */
@ManagedBean(name = "perspectivasEvaluacion")
@ViewScoped
public class PerspectivasEvaluacionBean implements Serializable {

    private static final long serialVersionUID = 1L;

    

    private Formulario formulario = new Formulario();
    private Formulario formularioInformantes = new Formulario();
    private Perspectivas perspectiva;
    private Informantes informante;
    private List<Perspectivas> listaPerspectivas;
    private List<Informantes> listaInformantes;
    private List<Informantes> listaInformantesBorrados;

//    private Oficinas area;
    private Organigrama organigrama;
    private Cargosxorganigrama cargo;
    private Codigos codigoPerspectiva;

    @EJB
    private PerspectivasFacade ejbPerspectivas;
    @EJB
    private InformantesFacade ejbInformantes;
    @EJB
    private CodigosFacade ejbCodigos;
    @EJB
    private CargosxorganigramaFacade ejbCargosOrganigramas;
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
    public PerspectivasEvaluacionBean() {
    }

    public String crear() {
       

        if (cargo == null) {
            MensajesErrores.advertencia("Seleccione cargo a ser evaluado");
            return null;
        }

        if (codigoPerspectiva == null) {
            MensajesErrores.advertencia("Seleccione perspectiva de evaluación");
            return null;
        }

        perspectiva = new Perspectivas();
        listaInformantes = new LinkedList<>();
        perspectiva.setActivo(Boolean.TRUE);
        perspectiva.setCargoevaluado(cargo);
        perspectiva.setPerspectiva(codigoPerspectiva);
        formulario.insertar();
        buscar();
        return null;
    }

    public String modificar() {
       
        perspectiva = listaPerspectivas.get(formulario.getFila().getRowIndex());
        formulario.setIndiceFila(formulario.getFila().getRowIndex());
        codigoPerspectiva = perspectiva.getPerspectiva();
        cargo = perspectiva.getCargoevaluado();
        listaInformantesBorrados = new LinkedList<>();
        buscarInformantes();
        formulario.editar();
        return null;
    }

    public String eliminar() {
        
        perspectiva = listaPerspectivas.get(formulario.getFila().getRowIndex());
        formulario.setIndiceFila(formulario.getFila().getRowIndex());
        codigoPerspectiva = perspectiva.getPerspectiva();
        cargo = perspectiva.getCargoevaluado();
        listaInformantesBorrados = new LinkedList<>();
        formulario.eliminar();
        buscarInformantes();
        return null;
    }

    private void buscarInformantes() {
        Map parametros = new LinkedHashMap();
        parametros.put(";where", " o.perspectiva=:perspectiva and o.activo=true");
        parametros.put("perspectiva", perspectiva);
        try {
            listaInformantes = ejbInformantes.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(PerspectivasEvaluacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String borrar() {
        perspectiva.setActivo(Boolean.FALSE);

        try {
            ejbPerspectivas.edit(perspectiva, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            Logger.getLogger(PerspectivasEvaluacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String cancelar() {
        formulario.cancelar();
        buscar();
        return null;
    }

    public String buscar() {
        
        if (cargo == null) {
            MensajesErrores.advertencia("Seleccione cargo");
            return null;
        }
        try {
            Map parametros = new HashMap();
            String where = "o.activo=true";

            if (cargo != null) {
                where += " and o.cargoevaluado=:cargo";
                parametros.put("cargo", cargo);
            }

            parametros.put(";where", where);
            listaPerspectivas = ejbPerspectivas.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(PerspectivasEvaluacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private boolean ponderacionMaxima() {
        BigDecimal suma = new BigDecimal(0);
        for (Perspectivas p : listaPerspectivas) {
            suma = suma.add(p.getPonderacion());
        }
        int ref;
        BigDecimal tope = BigDecimal.valueOf(100.00);
        ref = suma.compareTo(tope);
        if (ref >= 1) {
            MensajesErrores.advertencia("No puede exceder " + tope);
            return true;
        }
        return false;
    }

    private boolean validar() {
        if (perspectiva.getPerspectiva() == null) {
            MensajesErrores.advertencia("Seleccione perspectiva de evaluación");
            return true;
        }
        if (perspectiva.getPonderacion() == null || perspectiva.getPonderacion().compareTo(BigDecimal.ZERO) < 1) {
            MensajesErrores.advertencia("La ponderación debe ser valor positivo");
            return true;
        }
        if (listaInformantes.isEmpty()) {
            MensajesErrores.advertencia("Ingrese al menos un informante");
            return true;
        }

        return false;
    }

    public String insertar() {

        if (validar()) {
            return null;
        }
        listaPerspectivas.add(perspectiva);
        if (ponderacionMaxima()) {
            return null;
        }
        try {
            ejbPerspectivas.create(perspectiva, parametrosSeguridad.getLogueado().getUserid());
            for (Informantes inf : listaInformantes) {
                ejbInformantes.create(inf, parametrosSeguridad.getLogueado().getUserid());
            }
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(PerspectivasEvaluacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String grabar() {
        if (validar()) {
            return null;
        }
        listaPerspectivas.set(formulario.getIndiceFila(), perspectiva);
        if (ponderacionMaxima()) {
            return null;
        }

        try {
            ejbPerspectivas.edit(perspectiva, parametrosSeguridad.getLogueado().getUserid());
            for (Informantes inf : listaInformantes) {
                if (inf.getId() == null) {
                    ejbInformantes.create(inf, parametrosSeguridad.getLogueado().getUserid());
                } else {
                    ejbInformantes.edit(inf, parametrosSeguridad.getLogueado().getUserid());
                }
            }
            for (Informantes inf : listaInformantesBorrados) {
                if (inf.getId() != null) {
                    inf.setActivo(Boolean.FALSE);
                    ejbInformantes.edit(inf, parametrosSeguridad.getLogueado().getUserid());
                }
            }
        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(PerspectivasEvaluacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String crearInformante() {
        if (perspectiva == null) {
            MensajesErrores.advertencia("Seleccione una perspectiva");
            return null;
        }
        informante = new Informantes();
        informante.setActivo(Boolean.TRUE);
        informante.setPerspectiva(perspectiva);
        formularioInformantes.insertar();
        return null;
    }

    public String modificarInformante() {
        informante = listaInformantes.get(formularioInformantes.getFila().getRowIndex());
        formularioInformantes.editar();
        return null;
    }

    public String borrarInformante() {
        informante = listaInformantes.get(formularioInformantes.getFila().getRowIndex());
        formularioInformantes.eliminar();
        return null;
    }

    private boolean validarInformante() {
        if (informante.getInformante() == null) {
            MensajesErrores.advertencia("Seleccione un informante");
            return true;
        }
        for (Informantes i : listaInformantes) {
            if (i.getInformante().equals(informante.getInformante())) {
                MensajesErrores.advertencia("Informante duplicado");
                return true;
            }
        }
        return false;
    }

    public String insertarInformante() {
        if (validarInformante()) {
            return null;
        }
        listaInformantes.add(informante);
        formularioInformantes.cancelar();
        return null;
    }

    public String grabarInformante() {
        if (validarInformante()) {
            return null;
        }
        listaInformantes.set(formularioInformantes.getFila().getRowIndex(), informante);
        formularioInformantes.cancelar();
        return null;
    }

    public String eliminarInformante() {
        listaInformantes.remove(informante);
        listaInformantesBorrados.add(informante);
        formularioInformantes.cancelar();
        return null;
    }

    public String getInformantes() {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", " o.perspectiva=:perspectiva and  o.activo=true");
            parametros.put("perspectiva", listaPerspectivas.get(formulario.getFila().getRowIndex()));

            List<Informantes> li = ejbInformantes.encontarParametros(parametros);

            String retorno = "<ul>";
            for (Informantes i : li) {
                retorno += "<li>" + i.getInformante().getCargo().getNombre() + "</li>";
            }

            return retorno + "</ul>";
        } catch (ConsultarException ex) {
            Logger.getLogger(PerspectivasEvaluacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboPerspectivas() {
        return Combos.getSelectItems(traerCodigos("PERS", null), true);
    }

    //codigos de procesos que  no consten en planificaciones
    private List<Codigos> traerCodigos(String maestro, String modulo) {

        List<Codigos> aux = new LinkedList<>();
        try {
            Map parametros = new HashMap();

            for (Codigos cod : ejbCodigos.traerCodigos(maestro)) {
                parametros.put(";where", "o.perspectiva=:codigo and o.cargoevaluado=:cargo and o.activo=true");
                parametros.put("codigo", cod);
                parametros.put("cargo", cargo);
                int i = ejbPerspectivas.contar(parametros);
                if (i == 0) {
                    aux.add(cod);
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(PerspectivasEvaluacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return aux;
    }

    public SelectItem[] getComboCargosOrganigrama() {
        Map parametros = new HashMap();
        List<Cargosxorganigrama> lista = new LinkedList<>();
        try {

            switch (perspectiva.getPerspectiva().getParametros()) {
                case "1"://Evaluación
                    parametros.put(";where", " o.reporta=:reporta");
                    parametros.put("reporta", cargo);
                    lista = ejbCargosOrganigramas.encontarParametros(parametros);
                    if (cargo.getReporta() != null) {
                        lista.add(cargo.getReporta());
                    }
                    break;
                case "0"://Autoevaluación
                    lista.add(cargo);
                    break;
                case "-1"://Coevaluación
                    parametros.put(";where", " o.organigrama=:organigrama and o.cargo.nivel=:nivel");
                    parametros.put("organigrama", cargo.getOrganigrama());
                    parametros.put("nivel", cargo.getCargo().getNivel());
                    lista = ejbCargosOrganigramas.encontarParametros(parametros);
                    break;
            }

            return Combos.getSelectItems(lista, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(PerspectivasEvaluacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public BigDecimal getTotal() {
        BigDecimal retorno = new BigDecimal(0);
        for (Perspectivas p : listaPerspectivas) {
            retorno = retorno.add(p.getPonderacion() != null ? p.getPonderacion() : new BigDecimal(BigInteger.ZERO));
        }
        return retorno;
    }

    public Perspectivas traer(Integer id) throws ConsultarException {
        return ejbPerspectivas.find(id);
    }

    /**
     * @return the parametrosSeguridad
     */
    public ParametrosSfccbdmqBean getSeguridadbean() {
        return parametrosSeguridad;
    }

    /**
     * @param parametrosSeguridad the parametrosSeguridad to set
     */
    public void setSeguridadbean(ParametrosSfccbdmqBean parametrosSeguridad) {
        this.parametrosSeguridad = parametrosSeguridad;
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
     * @return the formularioInformantes
     */
    public Formulario getFormularioInformantes() {
        return formularioInformantes;
    }

    /**
     * @param formularioInformantes the formularioInformantes to set
     */
    public void setFormularioInformantes(Formulario formularioInformantes) {
        this.formularioInformantes = formularioInformantes;
    }

    /**
     * @return the perspectiva
     */
    public Perspectivas getPerspectiva() {
        return perspectiva;
    }

    /**
     * @param perspectiva the perspectiva to set
     */
    public void setPerspectiva(Perspectivas perspectiva) {
        this.perspectiva = perspectiva;
    }

    /**
     * @return the informante
     */
    public Informantes getInformante() {
        return informante;
    }

    /**
     * @param informante the informante to set
     */
    public void setInformante(Informantes informante) {
        this.informante = informante;
    }

    /**
     * @return the listaPerspectivas
     */
    public List<Perspectivas> getListaPerspectivas() {
        return listaPerspectivas;
    }

    /**
     * @param listaPerspectivas the listaPerspectivas to set
     */
    public void setListaPerspectivas(List<Perspectivas> listaPerspectivas) {
        this.listaPerspectivas = listaPerspectivas;
    }

    /**
     * @return the listaInformantes
     */
    public List<Informantes> getListaInformantes() {
        return listaInformantes;
    }

    /**
     * @param listaInformantes the listaInformantes to set
     */
    public void setListaInformantes(List<Informantes> listaInformantes) {
        this.listaInformantes = listaInformantes;
    }

    /**
     * @return the listaInformantesBorrados
     */
    public List<Informantes> getListaInformantesBorrados() {
        return listaInformantesBorrados;
    }

    /**
     * @param listaInformantesBorrados the listaInformantesBorrados to set
     */
    public void setListaInformantesBorrados(List<Informantes> listaInformantesBorrados) {
        this.listaInformantesBorrados = listaInformantesBorrados;
    }

//    /**
//     * @return the area
//     */
//    public Oficinas getArea() {
//        return area;
//    }
//
//    /**
//     * @param area the area to set
//     */
//    public void setArea(Oficinas area) {
//        this.area = area;
//    }
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
     * @return the codigoPerspectiva
     */
    public Codigos getCodigoPerspectiva() {
        return codigoPerspectiva;
    }

    /**
     * @param codigoPerspectiva the codigoPerspectiva to set
     */
    public void setCodigoPerspectiva(Codigos codigoPerspectiva) {
        this.codigoPerspectiva = codigoPerspectiva;
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

}
