/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;


import org.seguridad.sfccbdmq.ConfiguracionBean;
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
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.EquivalenciasevFacade;
import org.beans.sfccbdmq.InformantesFacade;
import org.beans.sfccbdmq.InformeevaluacionFacade;
import org.beans.sfccbdmq.PreguntasFacade;
import org.beans.sfccbdmq.PreguntasinformeFacade;
import org.beans.sfccbdmq.RespuestasFacade;
import org.entidades.sfccbdmq.Configuracionesev;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Equivalenciasev;
import org.entidades.sfccbdmq.Informantes;
import org.entidades.sfccbdmq.Informeevaluacion;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Perspectivas;
import org.entidades.sfccbdmq.Preguntas;
import org.entidades.sfccbdmq.Preguntasinforme;
import org.entidades.sfccbdmq.Respuestas;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author luisfernando
 */
@ManagedBean(name = "evaluacionEmpleado")
@ViewScoped
public class EvaluacionEmpleadoBean implements Serializable {

    private static final long serialVersionUID = 1L;


    private Formulario formulario = new Formulario();
    private Empleados empleado;
    private Empleados empleadoAEvaluar;
    private Informeevaluacion informe;
    private Configuracionesev configuracion;

    private List<Preguntasinforme> listaPreguntas;
    private List<Empleados> listaEmpleadosAEvaluar;

    @EJB
    private PreguntasFacade ejbPreguntas;
    @EJB
    private PreguntasinformeFacade ejbPreguntasInforme;
    @EJB
    private InformantesFacade ejbInformantes;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private InformeevaluacionFacade ejbInformes;
    @EJB
    private RespuestasFacade ejbRespuestas;
    @EJB
    private EquivalenciasevFacade ejbEquivalencias;
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
    public EvaluacionEmpleadoBean() {
    }

    public String cerrar() {
        return "/sistema/BlancoVista.jsf?faces-redirect=true";
    }

    

    public String buscar() {
        if (configuracion == null) {
            MensajesErrores.advertencia("Seleccione evaluación");
            return null;
        }

        if (empleado == null) {
            MensajesErrores.advertencia("Opción sólo para empleados!");
            return null;
        }

        listaEmpleadosAEvaluar = new LinkedList<>();
        List<Empleados> le = new LinkedList<>();
        Map parametros = new HashMap();
//        parametros.put(";where", " o.informante=:informante and o.perspectiva.cargoevaluado.cargo.nivel=:nivel and o.perspectiva.activo = true ");
        parametros.put(";where", " o.informante=:informante and o.perspectiva.activo = true and o.perspectiva.cargoevaluado.cargo.nivel=:nivel");
        parametros.put("informante", empleado.getCargoactual());
        parametros.put("nivel", configuracion.getFormulario().getNivelgestion());
        try {
            List<Perspectivas> lp = new LinkedList<>();
            for (Informantes inf : ejbInformantes.encontarParametros(parametros)) {
                lp.add(inf.getPerspectiva());
            }
            for (Perspectivas p : lp) {
                parametros = new HashMap();
                parametros.put(";where", " o.cargoactual=:cargo and o.activo=true");
                parametros.put("cargo", p.getCargoevaluado());
                parametros.put(";orden", " o.entidad.id");
                le = ejbEmpleados.encontarParametros(parametros);
            }
            Empleados aux = null;
            for (Empleados e : le) {
                if (aux == null) {
                    aux = e;
                    listaEmpleadosAEvaluar.add(e);
                } else if (!e.equals(aux)) {
                    listaEmpleadosAEvaluar.add(e);
                }

            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " - " + ex.getCause());
            Logger.getLogger(EvaluacionEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String evaluar() {
        empleadoAEvaluar = listaEmpleadosAEvaluar.get(formulario.getFila().getRowIndex());
        informe = new Informeevaluacion();
        informe.setEmpleado(empleadoAEvaluar);

        Map parametros = new HashMap();
        parametros.put(";where", " o.formulario=:formulario and o.activo=true");
        parametros.put("formulario", configuracion.getFormulario());
        listaPreguntas = new LinkedList<>();
        List<Preguntas> lista;
        try {
            lista = ejbPreguntas.encontarParametros(parametros);
            if (lista.isEmpty()) {
                MensajesErrores.advertencia("Formulario inexistente");
                return null;
            }

            for (Preguntas p : lista) {
                Preguntasinforme pinf = new Preguntasinforme();
                pinf.setInforme(informe);
                pinf.setPregunta(p);
                listaPreguntas.add(pinf);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " - " + ex.getCause());
            Logger.getLogger(EvaluacionEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.insertar();
        return null;
    }

    public String verInforme() {
        Empleados e = listaEmpleadosAEvaluar.get(formulario.getFila().getRowIndex());
        Map parametros = new HashMap();
        parametros.put(";where", " o.empleado=:empleado and o.evaluacion=:evaluacion and o.informante=:informante");
        parametros.put("empleado", e);
        parametros.put("evaluacion", configuracion);
        parametros.put("informante", parametrosSeguridad.getLogueado());

        List<Informeevaluacion> i;
        try {
            i = ejbInformes.encontarParametros(parametros);
            if (!i.isEmpty()) {
                informe = i.get(0);
            }
            parametros = new HashMap();
            parametros.put(";where", " o.informe=:informe");
            parametros.put("informe", informe);

            listaPreguntas = ejbPreguntasInforme.encontarParametros(parametros);

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " - " + ex.getCause());
            Logger.getLogger(EvaluacionEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formulario.editar();
        return null;
    }

    private boolean validar() {
        for (Preguntasinforme pi : listaPreguntas) {
            if (pi.getRespuestaPregunta() == null) {
                MensajesErrores.advertencia("La pregunta: " + pi.getPregunta().getOrden() + " tiene que ser respondida");
                return true;
            }
        }
        return false;
    }

    public String getRespuesta(Preguntasinforme p) {
        Preguntasinforme pi = p;
        try {
            Respuestas r = ejbRespuestas.find(pi.getRespuesta());
            return r.getRespuestas();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " - " + ex.getCause());
            Logger.getLogger(EvaluacionEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public boolean estado() {
        Empleados e = listaEmpleadosAEvaluar.get(formulario.getFila().getRowIndex());
        Map parametros = new HashMap();
        parametros.put(";where", " o.empleado=:empleado and o.evaluacion=:evaluacion and o.informante=:informante");
        parametros.put("empleado", e);
        parametros.put("evaluacion", configuracion);
        parametros.put("informante", parametrosSeguridad.getLogueado());
        try {
            if (ejbInformes.contar(parametros) > 0) {
                return true;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " - " + ex.getCause());
            Logger.getLogger(EvaluacionEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public String resultado() {
        Empleados e = listaEmpleadosAEvaluar.get(formulario.getFila().getRowIndex());
        Map parametros = new HashMap();
        parametros.put(";where", " o.empleado=:empleado and o.evaluacion=:evaluacion and o.informante=:informante");
        parametros.put("empleado", e);
        parametros.put("evaluacion", configuracion);
        parametros.put("informante", parametrosSeguridad.getLogueado());
        try {
            List<Informeevaluacion> lista = ejbInformes.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                parametros = new HashMap();
                parametros.put(";where", " o.informe=:informe");
                parametros.put("informe", lista.get(0));

                Integer resultado = 0;
                double calificacion = 0;
                Integer sobre = 0;
                double fijo = 0;
                String equivalencia = "";

                for (Preguntasinforme pi : ejbPreguntasInforme.encontarParametros(parametros)) {
                    resultado += ejbRespuestas.find(pi.getRespuesta()).getOrden();
                    parametros = new HashMap();
                    parametros.put(";where", " o.pregunta=:pregunta and o.activo = true");
                    parametros.put("pregunta", pi.getPregunta());
                    parametros.put(";orden", "o.orden desc");
                    sobre += ejbRespuestas.encontarParametros(parametros).get(0).getOrden();
                }

                parametros = new HashMap();
                parametros.put(";where", "  o.activo=true");

                calificacion = resultado * 100 / sobre;

                for (Equivalenciasev eq : ejbEquivalencias.encontarParametros(parametros)) {
                    if (calificacion >= eq.getNotaminima().doubleValue() && calificacion <= eq.getNotamaxima().doubleValue()) {
                        equivalencia = eq.getDescripcion();
                        fijo = eq.getPuntajefijo().doubleValue();
                    }
                }

                return fijo + "  -  " + equivalencia;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " - " + ex.getCause());
            Logger.getLogger(EvaluacionEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String grabar() {
        if (validar()) {
            return null;
        }
        try {
            informe.setFecha(new Date());
            informe.setInformante(parametrosSeguridad.getLogueado());
            informe.setEvaluacion(configuracion);
            ejbInformes.create(informe, parametrosSeguridad.getLogueado().getUserid());
            for (Preguntasinforme pinf : listaPreguntas) {
                if (pinf.getId() == null) {
                    pinf.setRespuesta(pinf.getRespuestaPregunta().getId());
                    ejbPreguntasInforme.create(pinf, parametrosSeguridad.getLogueado().getUserid());
                } else {
                    pinf.setRespuesta(pinf.getRespuestaPregunta().getId());
                    ejbPreguntasInforme.edit(pinf, parametrosSeguridad.getLogueado().getUserid());
                }
            }

        } catch (InsertarException | GrabarException ex) {
            Logger.getLogger(EvaluacionEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
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
     * @return the empleado
     */
    public Empleados getEmpleado() {
        return empleado;
    }

    /**
     * @param empleado the empleado to set
     */
    public void setEmpleado(Empleados empleado) {
        this.empleado = empleado;
    }

    /**
     * @return the informe
     */
    public Informeevaluacion getInforme() {
        return informe;
    }

    /**
     * @param informe the informe to set
     */
    public void setInforme(Informeevaluacion informe) {
        this.informe = informe;
    }

    /**
     * @return the listaPreguntas
     */
    public List<Preguntasinforme> getListaPreguntas() {
        return listaPreguntas;
    }

    /**
     * @param listaPreguntas the listaPreguntas to set
     */
    public void setListaPreguntas(List<Preguntasinforme> listaPreguntas) {
        this.listaPreguntas = listaPreguntas;
    }

    /**
     * @return the empleadoAEvaluar
     */
    public Empleados getEmpleadoAEvaluar() {
        return empleadoAEvaluar;
    }

    /**
     * @param empleadoAEvaluar the empleadoAEvaluar to set
     */
    public void setEmpleadoAEvaluar(Empleados empleadoAEvaluar) {
        this.empleadoAEvaluar = empleadoAEvaluar;
    }

    /**
     * @return the listaEmpleadosAEvaluar
     */
    public List<Empleados> getListaEmpleadosAEvaluar() {
        return listaEmpleadosAEvaluar;
    }

    /**
     * @param listaEmpleadosAEvaluar the listaEmpleadosAEvaluar to set
     */
    public void setListaEmpleadosAEvaluar(List<Empleados> listaEmpleadosAEvaluar) {
        this.listaEmpleadosAEvaluar = listaEmpleadosAEvaluar;
    }

    /**
     * @return the configuracion
     */
    public Configuracionesev getConfiguracion() {
        return configuracion;
    }

    /**
     * @param configuracion the configuracion to set
     */
    public void setConfiguracion(Configuracionesev configuracion) {
        this.configuracion = configuracion;
    }

    public boolean estado(Empleados e) {
        Map parametros = new HashMap();
        parametros.put(";where", " o.empleado=:empleado and o.evaluacion=:evaluacion");
        parametros.put("empleado", e);
        parametros.put("evaluacion", configuracion);
        try {
            if (ejbInformes.contar(parametros) > 0) {
                return true;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " - " + ex.getCause());
            Logger.getLogger(EvaluacionEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public String resultado(Empleados e) {
        Map parametros = new HashMap();
        parametros.put(";where", " o.empleado=:empleado and o.evaluacion=:evaluacion");
        parametros.put("empleado", e);
        parametros.put("evaluacion", configuracion);
        try {
            List<Informeevaluacion> lista = ejbInformes.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                parametros = new HashMap();
                parametros.put(";where", " o.informe=:informe");
                parametros.put("informe", lista.get(0));

                Integer resultado = 0;
                double calificacion = 0;
                Integer sobre = 0;
                double fijo = 0;
                String equivalencia = "";

                for (Preguntasinforme pi : ejbPreguntasInforme.encontarParametros(parametros)) {
                    resultado += ejbRespuestas.find(pi.getRespuesta()).getOrden();
                    parametros = new HashMap();
                    parametros.put(";where", " o.pregunta=:pregunta and o.activo = true");
                    parametros.put("pregunta", pi.getPregunta());
                    parametros.put(";orden", "o.orden desc");
                    sobre += ejbRespuestas.encontarParametros(parametros).get(0).getOrden();
                }

                parametros = new HashMap();
                parametros.put(";where", "o.activo=true");

                calificacion = resultado * 100 / sobre;

                for (Equivalenciasev eq : ejbEquivalencias.encontarParametros(parametros)) {
                    if (calificacion >= eq.getNotaminima().doubleValue() && calificacion <= eq.getNotamaxima().doubleValue()) {
                        equivalencia = eq.getDescripcion();
                        fijo = eq.getPuntajefijo().doubleValue();
                    }
                }

                return fijo + "  -  " + equivalencia;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " - " + ex.getCause());
            Logger.getLogger(EvaluacionEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String verInforme(Empleados e) {
        Map parametros = new HashMap();
        parametros.put(";where", " o.empleado=:empleado and o.evaluacion=:evaluacion and o.informante=:informante");
        parametros.put("empleado", e);
        parametros.put("evaluacion", configuracion);
        parametros.put("informante", parametrosSeguridad.getLogueado());

        List<Informeevaluacion> i;
        try {
            i = ejbInformes.encontarParametros(parametros);
            if (!i.isEmpty()) {
                informe = i.get(0);
            }
            parametros = new HashMap();
            parametros.put(";where", " o.informe=:informe");
            parametros.put("informe", informe);

            listaPreguntas = ejbPreguntasInforme.encontarParametros(parametros);

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " - " + ex.getCause());
            Logger.getLogger(EvaluacionEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formulario.editar();
        return null;
    }

    public String verReporteInforme(Empleados e) {
        Map parametros = new HashMap();
        parametros.put(";where", " o.empleado=:empleado and o.evaluacion=:evaluacion");
        parametros.put("empleado", e);
        parametros.put("evaluacion", configuracion);

        List<Informeevaluacion> i;
        try {
            i = ejbInformes.encontarParametros(parametros);
            if (!i.isEmpty()) {
                informe = i.get(0);
            }
            parametros = new HashMap();
            parametros.put(";where", " o.informe=:informe");
            parametros.put("informe", informe);

            listaPreguntas = ejbPreguntasInforme.encontarParametros(parametros);

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " - " + ex.getCause());
            Logger.getLogger(EvaluacionEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formulario.editar();
        return null;
    }

}
