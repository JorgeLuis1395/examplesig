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
import javax.faces.model.SelectItem;
import org.beans.sfccbdmq.FormulariosFacade;
import org.beans.sfccbdmq.PreguntasFacade;
import org.beans.sfccbdmq.RespuestasFacade;
import org.entidades.sfccbdmq.Formularios;
import org.entidades.sfccbdmq.Nivelesgestion;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Preguntas;
import org.entidades.sfccbdmq.Respuestas;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author luisfernando
 */
@ManagedBean(name = "preguntas")
@ViewScoped
public class PreguntasBean implements Serializable {

    private static final long serialVersionUID = 1L;

    

    private Formulario formulario = new Formulario();
    private Formulario formularioPreguntas = new Formulario();
    private Formulario formularioRespuestas = new Formulario();
    private Preguntas pregunta;
    private Respuestas respuesta;

    private List<Preguntas> listaPreguntas;
    private List<Preguntas> listaPreguntasBorradas;

    private List<Respuestas> listaRespuestas;
    private List<Respuestas> listaRespuestasBorradas;
    private List<Formularios> listaFormularios;

    private Nivelesgestion nivelgestion;
    private Formularios formularioNiveles;
    @EJB
    private PreguntasFacade ejbPreguntas;
    @EJB
    private RespuestasFacade ejbRespuestas;
    @EJB
    private FormulariosFacade ejbFormularios;
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
    public PreguntasBean() {
    }

    public String buscarFormularios() {
        Map parametros = new HashMap();
        parametros.put(";where", " o.nivelgestion=:nivelgestion and o.activo=true");
        parametros.put("nivelgestion", nivelgestion);
        try {
            listaFormularios = ejbFormularios.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(PreguntasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String cancelar() {
        formulario.cancelar();
        formularioNiveles = null;
        buscarFormularios();
        return null;
    }

    public String cerrar() {
        return "/sistema/BlancoVista.jsf?faces-redirect=true";
    }

    private boolean validar() {
        if (formularioNiveles.getNombre() == null || formularioNiveles.getNombre().isEmpty()) {
            MensajesErrores.advertencia("Ingrese nombre del formulario");
            return true;
        }
        if (listaPreguntas.isEmpty()) {
            MensajesErrores.advertencia("Ingrese al menos una pregunta");
            return true;
        }
        return false;
    }

    private boolean validarPregunta() {
        if (pregunta.getOrden() == null) {
            MensajesErrores.advertencia("Ingrese orden de la pregunta");
            return true;
        }
        if (pregunta.getPregunta() == null || pregunta.getPregunta().isEmpty()) {
            MensajesErrores.advertencia("Ingrese el texto de la pregunta");
            return true;
        }
        if (listaRespuestas.isEmpty()) {
            MensajesErrores.advertencia("Ingrese al menos una referencia");
            return true;
        }
        return false;
    }

    private boolean validarRespuestas() {
        if (respuesta.getOrden() == null) {
            MensajesErrores.advertencia("Ingrese orden de la referencia");
            return true;
        }
        if (respuesta.getRespuestas() == null || respuesta.getRespuestas().isEmpty()) {
            MensajesErrores.advertencia("Ingrese el texto de la referencia");
            return true;
        }
        return false;
    }

    public String crear() {
        if (nivelgestion == null) {
            MensajesErrores.advertencia("Seleccione Nivel de Gestión");
            return null;
        }
        formularioNiveles = new Formularios();
        formularioNiveles.setActivo(Boolean.TRUE);
        formularioNiveles.setNivelgestion(nivelgestion);
        formularioNiveles.setFecha(new Date());

        listaPreguntas = new LinkedList<>();
        listaRespuestas = new LinkedList<>();

        formulario.insertar();

        return null;
    }

    public String editar() {
        formularioNiveles = listaFormularios.get(formulario.getFila().getRowIndex());
        Map parametros = new HashMap();
        parametros.put(";where", " o.formulario=:formulario and o.activo=true");
        parametros.put("formulario", formularioNiveles);
        parametros.put(";orden", "o.orden");

        try {
            listaPreguntas = ejbPreguntas.encontarParametros(parametros);
            for (Preguntas p : listaPreguntas) {
                parametros = new HashMap();
                parametros.put(";where", "o.pregunta=:pregunta");
                parametros.put("pregunta", p);
                p.setListaRespuestas(ejbRespuestas.encontarParametros(parametros));
                p.setListaRespuestasBorradas(new LinkedList<Respuestas>());
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(PreguntasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.editar();
        return null;
    }

    public String borrar() {
        formularioNiveles = listaFormularios.get(formulario.getFila().getRowIndex());
        Map parametros = new HashMap();
        parametros.put(";where", " o.formulario=:formulario and o.activo=true");
        parametros.put("formulario", formularioNiveles);
        parametros.put(";orden", "o.orden");

        try {
            listaPreguntas = ejbPreguntas.encontarParametros(parametros);
            for (Preguntas p : listaPreguntas) {
                parametros = new HashMap();
                parametros.put(";where", "o.pregunta=:pregunta");
                parametros.put("pregunta", p);
                p.setListaRespuestas(ejbRespuestas.encontarParametros(parametros));
                p.setListaRespuestasBorradas(new LinkedList<Respuestas>());
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(PreguntasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.eliminar();
        return null;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            ejbFormularios.create(formularioNiveles, parametrosSeguridad.getLogueado().getUserid());
            for (Preguntas p : listaPreguntas) {
                p.setFormulario(formularioNiveles);
                ejbPreguntas.create(p, parametrosSeguridad.getLogueado().getUserid());
                for (Respuestas r : p.getListaRespuestas()) {
                    ejbRespuestas.create(r, parametrosSeguridad.getLogueado().getUserid());
                }
            }
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(PreguntasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        formularioNiveles = null;
        buscarFormularios();
        return null;
    }

    public String grabar() {
        if (validar()) {
            return null;
        }
        try {
            ejbFormularios.edit(formularioNiveles, parametrosSeguridad.getLogueado().getUserid());
            for (Preguntas p : listaPreguntas) {
                if (p.getId() == null) {
                    ejbPreguntas.create(p, parametrosSeguridad.getLogueado().getUserid());
                } else {
                    ejbPreguntas.edit(p, parametrosSeguridad.getLogueado().getUserid());
                }

                for (Respuestas r : p.getListaRespuestas()) {
                    if (r.getId() == null) {
                        ejbRespuestas.create(r, parametrosSeguridad.getLogueado().getUserid());
                    } else {
                        ejbRespuestas.edit(r, parametrosSeguridad.getLogueado().getUserid());
                    }
                }
                if (p.getListaRespuestasBorradas() != null) {
                    for (Respuestas r : p.getListaRespuestasBorradas()) {
                        if (r.getId() != null) {
                            r.setActivo(Boolean.FALSE);
                            ejbRespuestas.edit(r, parametrosSeguridad.getLogueado().getUserid());
                        }
                    }
                }

            }
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(PreguntasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        formularioNiveles = null;
        buscarFormularios();
        return null;
    }

    public String eliminar() {
        if (validar()) {
            return null;
        }
        try {
            formularioNiveles.setActivo(Boolean.FALSE);
            ejbFormularios.edit(formularioNiveles, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(PreguntasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        formularioNiveles = null;
        buscarFormularios();
        return null;
    }

    public String verPreguntas() {
        formularioNiveles = listaFormularios.get(formulario.getFila().getRowIndex());
        Map parametros = new HashMap();
        parametros.put(";where", " o.formulario=:formulario and o.activo=true");
        parametros.put("formulario", formularioNiveles);
        parametros.put(";orden", "o.orden");

        try {
            listaPreguntas = ejbPreguntas.encontarParametros(parametros);
            for (Preguntas p : listaPreguntas) {
                parametros = new HashMap();
                parametros.put(";where", "o.pregunta=:pregunta");
                parametros.put("pregunta", p);
                p.setListaRespuestas(ejbRespuestas.encontarParametros(parametros));
                p.setListaRespuestasBorradas(new LinkedList<Respuestas>());
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(PreguntasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String crearPregunta() {
        pregunta = new Preguntas();
        pregunta.setActivo(Boolean.TRUE);
        pregunta.setFormulario(formularioNiveles);
        pregunta.setListaRespuestas(new LinkedList<Respuestas>());
        listaPreguntasBorradas = new LinkedList<>();
        listaRespuestas = new LinkedList<>();
        formularioPreguntas.insertar();
        return null;
    }

    public String modificarPregunta() {

        pregunta = listaPreguntas.get(formularioPreguntas.getFila().getRowIndex());
        formularioPreguntas.setIndiceFila(formularioPreguntas.getFila().getRowIndex());
        if (pregunta.getListaRespuestasBorradas() == null) {
            pregunta.setListaRespuestasBorradas(new LinkedList<Respuestas>());
        }
        listaRespuestas = pregunta.getListaRespuestas();
        listaRespuestasBorradas = pregunta.getListaRespuestasBorradas() != null ? pregunta.getListaRespuestasBorradas() : new LinkedList<Respuestas>();

        formularioPreguntas.editar();
        return null;
    }

    public String borrarPregunta() {

        pregunta = listaPreguntas.get(formularioPreguntas.getFila().getRowIndex());
        formularioPreguntas.setIndiceFila(formularioPreguntas.getFila().getRowIndex());
        if (pregunta.getListaRespuestasBorradas() == null) {
            pregunta.setListaRespuestasBorradas(new LinkedList<Respuestas>());
        }
        listaRespuestas = pregunta.getListaRespuestas();
        listaRespuestasBorradas = pregunta.getListaRespuestasBorradas();

        formularioPreguntas.eliminar();
        return null;
    }

    public String insertarPregunta() {
        if (validarPregunta()) {
            return null;
        }
        pregunta.setFormulario(formularioNiveles);
        pregunta.setListaRespuestas(listaRespuestas);
        listaPreguntas.add(pregunta);
        formularioPreguntas.cancelar();
        return null;
    }

    public String grabarPregunta() {
        if (validarPregunta()) {
            return null;
        }
        listaPreguntas.set(formularioPreguntas.getIndiceFila(), pregunta);
        pregunta.setListaRespuestasBorradas(listaRespuestasBorradas);
        formularioPreguntas.cancelar();
        return null;
    }

    public String eliminarPregunta() {
        listaPreguntas.remove(formularioPreguntas.getIndiceFila());
        listaPreguntasBorradas.add(pregunta);
        pregunta.setListaRespuestasBorradas(listaRespuestasBorradas);
        formularioPreguntas.cancelar();
        return null;
    }

    public String crearRespuesta() {
        respuesta = new Respuestas();
        respuesta.setPregunta(pregunta);
        respuesta.setActivo(Boolean.TRUE);
        formularioRespuestas.insertar();
        return null;
    }

    public String modificarRespuesta() {
        respuesta = listaRespuestas.get(formularioRespuestas.getFila().getRowIndex());
        formularioRespuestas.setIndiceFila(formularioRespuestas.getFila().getRowIndex());
        formularioRespuestas.editar();
        return null;
    }

    public String borrarRespuesta() {
        respuesta = listaRespuestas.get(formularioRespuestas.getFila().getRowIndex());
        formularioRespuestas.setIndiceFila(formularioRespuestas.getFila().getRowIndex());
        formularioRespuestas.eliminar();
        return null;
    }

    public String insertarRespuesta() {
        if (validarRespuestas()) {
            return null;
        }
        listaRespuestas.add(respuesta);
        formularioRespuestas.cancelar();
        return null;
    }

    public String grabarRespuesta() {
        if (validarRespuestas()) {
            return null;
        }
        listaRespuestas.set(formularioRespuestas.getIndiceFila(), respuesta);
        formularioRespuestas.cancelar();
        return null;
    }

    public String eliminarRespuesta() {
        listaRespuestas.remove(formularioRespuestas.getIndiceFila());
        listaRespuestasBorradas.add(respuesta);
        formularioRespuestas.cancelar();
        return null;
    }

    public Respuestas traer(Integer id) throws ConsultarException {
        return ejbRespuestas.find(id);
    }

    public Formularios traerForm(Integer id) throws ConsultarException {
        return ejbFormularios.find(id);
    }

    public SelectItem[] comboRespuestas(Preguntas p) {
        Map parametros = new HashMap();
        parametros.put(";where", " o.pregunta=:pregunta and o.activo = true");
        parametros.put("pregunta", p);
        parametros.put(";orden", " o.orden desc");
        try {
            return Combos.getSelectItems(ejbRespuestas.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(PreguntasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboFormularios() {
        Map parametros = new HashMap();
        parametros.put(";where", " o.nivelgestion=:nivelgestion and o.activo = true");
        parametros.put("nivelgestion", nivelgestion);
        try {
            return Combos.getSelectItems(ejbFormularios.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(PreguntasBean.class.getName()).log(Level.SEVERE, null, ex);
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
     * @return the formularioPreguntas
     */
    public Formulario getFormularioPreguntas() {
        return formularioPreguntas;
    }

    /**
     * @param formularioPreguntas the formularioPreguntas to set
     */
    public void setFormularioPreguntas(Formulario formularioPreguntas) {
        this.formularioPreguntas = formularioPreguntas;
    }

    /**
     * @return the formularioRespuestas
     */
    public Formulario getFormularioRespuestas() {
        return formularioRespuestas;
    }

    /**
     * @param formularioRespuestas the formularioRespuestas to set
     */
    public void setFormularioRespuestas(Formulario formularioRespuestas) {
        this.formularioRespuestas = formularioRespuestas;
    }

    /**
     * @return the pregunta
     */
    public Preguntas getPregunta() {
        return pregunta;
    }

    /**
     * @param pregunta the pregunta to set
     */
    public void setPregunta(Preguntas pregunta) {
        this.pregunta = pregunta;
    }

    /**
     * @return the respuesta
     */
    public Respuestas getRespuesta() {
        return respuesta;
    }

    /**
     * @param respuesta the respuesta to set
     */
    public void setRespuesta(Respuestas respuesta) {
        this.respuesta = respuesta;
    }

    /**
     * @return the listaPreguntas
     */
    public List<Preguntas> getListaPreguntas() {
        return listaPreguntas;
    }

    /**
     * @param listaPreguntas the listaPreguntas to set
     */
    public void setListaPreguntas(List<Preguntas> listaPreguntas) {
        this.listaPreguntas = listaPreguntas;
    }

    /**
     * @return the listaPreguntasBorradas
     */
    public List<Preguntas> getListaPreguntasBorradas() {
        return listaPreguntasBorradas;
    }

    /**
     * @param listaPreguntasBorradas the listaPreguntasBorradas to set
     */
    public void setListaPreguntasBorradas(List<Preguntas> listaPreguntasBorradas) {
        this.listaPreguntasBorradas = listaPreguntasBorradas;
    }

    /**
     * @return the listaRespuestas
     */
    public List<Respuestas> getListaRespuestas() {
        return listaRespuestas;
    }

    /**
     * @param listaRespuestas the listaRespuestas to set
     */
    public void setListaRespuestas(List<Respuestas> listaRespuestas) {
        this.listaRespuestas = listaRespuestas;
    }

    /**
     * @return the listaRespuestasBorradas
     */
    public List<Respuestas> getListaRespuestasBorradas() {
        return listaRespuestasBorradas;
    }

    /**
     * @param listaRespuestasBorradas the listaRespuestasBorradas to set
     */
    public void setListaRespuestasBorradas(List<Respuestas> listaRespuestasBorradas) {
        this.listaRespuestasBorradas = listaRespuestasBorradas;
    }

    /**
     * @return the formularioNiveles
     */
    public Formularios getFormularioNiveles() {
        return formularioNiveles;
    }

    /**
     * @param formularioNiveles the formularioNiveles to set
     */
    public void setFormularioNiveles(Formularios formularioNiveles) {
        this.formularioNiveles = formularioNiveles;
    }

    /**
     * @return the nivelgestion
     */
    public Nivelesgestion getNivelgestion() {
        return nivelgestion;
    }

    /**
     * @param nivelgestion the nivelgestion to set
     */
    public void setNivelgestion(Nivelesgestion nivelgestion) {
        this.nivelgestion = nivelgestion;
    }

    /**
     * @return the listaFormularios
     */
    public List<Formularios> getListaFormularios() {
        return listaFormularios;
    }

    /**
     * @param listaFormularios the listaFormularios to set
     */
    public void setListaFormularios(List<Formularios> listaFormularios) {
        this.listaFormularios = listaFormularios;
    }

}
