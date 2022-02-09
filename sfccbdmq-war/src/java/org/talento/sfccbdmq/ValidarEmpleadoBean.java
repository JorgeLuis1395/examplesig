/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;;

import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.utilitarios.sfccbdmq.Recurso;
import javax.faces.application.Resource;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
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
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.beans.sfccbdmq.CargosFacade;
import org.beans.sfccbdmq.CursosFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.EstudiosFacade;
import org.beans.sfccbdmq.ExperienciasFacade;
import org.beans.sfccbdmq.HabilidadesFacade;
import org.beans.sfccbdmq.IdiomasFacade;
import org.beans.sfccbdmq.RecomendacionesFacade;
import org.beans.sfccbdmq.ValoracionesdesempenioFacade;
import org.beans.sfccbdmq.VerificacionesdesempenioFacade;
import org.beans.sfccbdmq.VerificacionesreferenciasFacade;
import org.entidades.sfccbdmq.Cargos;
import org.entidades.sfccbdmq.Cursos;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Estudios;
import org.entidades.sfccbdmq.Experiencias;
import org.entidades.sfccbdmq.Habilidades;
import org.entidades.sfccbdmq.Idiomas;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Recomendaciones;
import org.entidades.sfccbdmq.Valoracionesdesempenio;
import org.entidades.sfccbdmq.Verificacionesdesempenio;
import org.entidades.sfccbdmq.Verificacionesreferencias;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author edison
 */
@ManagedBean(name = "validarEmpleado")
@ViewScoped
public class ValidarEmpleadoBean implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * Creates a new instance of ValidarEmpleadoBean
     */
    
    private LazyDataModel<Empleados> listaEmpleados;
    private Formulario formulario = new Formulario();
    private List<Cursos> listaCursos;
    private Cursos curso;
    private Formulario formularioC = new Formulario();
    private List<Estudios> listaEstudios;
    private Estudios estudio;
    private Formulario formularioE = new Formulario();
    private List<Idiomas> listaIdiomas;
    private Idiomas idioma;
    private Formulario formularioI = new Formulario();
    private List<Experiencias> listaExperiencias;
    private Experiencias experiencia;
    private Formulario formularioEx = new Formulario();
    private List<Habilidades> listaHabilidades;
    private Habilidades habilidad;
    private Formulario formularioH = new Formulario();
    private List<Recomendaciones> listaPersonales;
    private Recomendaciones recomendacion;
    private Formulario formularioP = new Formulario();
    private Cargos cargo;
    private String cedula;
    private Empleados empleado;
    private String Nombrearchivo;
    private Resource imagenrs;
    private Date fecha;
    private Formulario formularioImagen = new Formulario();
    private Verificacionesreferencias laboral;
    private Verificacionesreferencias personal;
    private Verificacionesdesempenio desempenio;
    private List<Verificacionesreferencias> listaVReferencias;
    private List<Verificacionesreferencias> listaVLaborales;
    private List<Verificacionesdesempenio> listaVDesempenio;
    private List<Valoracionesdesempenio> listaValoraciones;
    private List<Integer> listaValores;
    private List<BigDecimal> listaNotasExp;
    private String nombre;
    private Integer notaEx;

    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private CursosFacade ejbCursos;
    @EJB
    private EstudiosFacade ejbEstudios;
    @EJB
    private IdiomasFacade ejbIdiomas;
    @EJB
    private ExperienciasFacade ejbExperiencias;
    @EJB
    private HabilidadesFacade ejbHabilidades;
    @EJB
    private CargosFacade ejbCargos;
    @EJB
    private RecomendacionesFacade ejbRecomendaciones;
    @EJB
    private ValoracionesdesempenioFacade ejbValoraciones;
    @EJB
    private VerificacionesdesempenioFacade ejbVDesempenio;
    @EJB
    private VerificacionesreferenciasFacade ejbVReferencias;
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
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.activo=true");
            listaValoraciones = ejbValoraciones.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(VerificacionReferenciasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    // fin perfiles
    

   

    public ValidarEmpleadoBean() {
        listaEmpleados = new LazyDataModel<Empleados>() {
            @Override
            public List<Empleados> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }


    //MODIFICACIÓN 
    public String buscar(Empleados emp) {
        listaCursos = new LinkedList<>();
        listaEstudios = new LinkedList<>();
        listaIdiomas = new LinkedList<>();
        listaExperiencias = new LinkedList<>();
        listaHabilidades = new LinkedList<>();
        listaPersonales = new LinkedList<>();
        formulario.insertar();
        curso = new Cursos();
        estudio = new Estudios();
        idioma = new Idiomas();
        experiencia = new Experiencias();
        habilidad = new Habilidades();
        recomendacion = new Recomendaciones();
        laboral = new Verificacionesreferencias();
        personal = new Verificacionesreferencias();
        desempenio = new Verificacionesdesempenio();
        empleado = emp;
        Map parametros = new HashMap();
        String where = "";
        if (empleado != null) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += "o.empleado=:empleado";
            parametros.put("empleado", empleado);
        }
        if (nombre != null && !nombre.isEmpty()) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += "upper(o.nombre)like:nomb";
            parametros.put("nomb", nombre.toUpperCase() + "%");
        }
        parametros.put(";where", where);
        try {
            parametros = new HashMap();
            parametros.put(";where", "o.empleado=:empleado");
            parametros.put("empleado", empleado);
            listaCursos = ejbCursos.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put(";where", "o.empleado=:empleado");
            parametros.put("empleado", empleado);
            listaEstudios = ejbEstudios.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put(";where", "o.empleado=:empleado");
            parametros.put("empleado", empleado);
            listaIdiomas = ejbIdiomas.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put(";where", "o.empleado=:empleado");
            parametros.put("empleado", empleado);
            listaExperiencias = ejbExperiencias.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put(";where", "o.empleado=:empleado");
            parametros.put("empleado", empleado);
            listaExperiencias = ejbExperiencias.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put(";where", "o.empleado=:empleado");
            parametros.put("empleado", empleado);
            listaPersonales = ejbRecomendaciones.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put(";where", "o.empleado=:empleado");
            parametros.put("empleado", empleado);
            listaHabilidades = ejbHabilidades.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " " + ex.getCause());
            Logger.getLogger(VerificacionReferenciasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

//CURSOS
    public String verificarCursos(Cursos cur, int index) {
        curso = cur;
        formularioC.setIndiceFila(index);
        formularioC.insertar();
        return null;
    }

    public String grabarCursos() {
        try {
            curso.setResponvalid(parametrosSeguridad.getLogueado().getUserid());
            ejbCursos.edit(curso, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            Logger.getLogger(ValidarEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Registro guardado");
        formularioC.cancelar();
        return null;
    }
//ESTUDIOS

    public String verificarEstudios(Estudios est, int index) {
        estudio = est;
        formularioE.setIndiceFila(index);
        formularioE.insertar();
        return null;
    }

    public String grabarEstudios() {
        try {
            estudio.setResponvalid(parametrosSeguridad.getLogueado().getUserid());
            ejbEstudios.edit(estudio, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            Logger.getLogger(ValidarEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Registro guardado");
        formularioE.cancelar();
        return null;
    }
//IDIOMAS

    public String verificarIdiomas(Idiomas idi, int index) {
        idioma = idi;
        formularioI.setIndiceFila(index);
        formularioI.insertar();
        return null;
    }

    public String grabarIdiomas() {
        try {
            idioma.setResponvalid(parametrosSeguridad.getLogueado().getUserid());
            ejbIdiomas.edit(idioma, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            Logger.getLogger(ValidarEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Registro guardado");
        formularioI.cancelar();
        return null;
    }
//EXPERIENCIAS

    public String verificarExperiencias(Experiencias exp, int index) {
        experiencia = exp;
        formularioEx.setIndiceFila(index);
        fecha = new Date();
        listaVDesempenio = new LinkedList<>();

        for (Valoracionesdesempenio vd : listaValoraciones) {
            try {
                Map parametros = new HashMap();
                parametros.put(";where", "o.experiencia=:experiencia and o.valoracion=:valoracion and o.activo=true");
                parametros.put("experiencia", experiencia);
                parametros.put("valoracion", vd);
                List<Verificacionesdesempenio> aux = ejbVDesempenio.encontarParametros(parametros);
                for (Verificacionesdesempenio valor : aux) {
                    if (valor != null) {
                        //getListaValoresVerificacionDesempeno(valor);
                        valor.setNota(valor.getValor());
                        listaVDesempenio.add(valor);
                    }
                }
                parametros = new HashMap();
                parametros.put(";where", "o.experiencia=:experiencia and o.activo=true");
                parametros.put("experiencia", experiencia);
                List<Verificacionesreferencias> reflab = ejbVReferencias.encontarParametros(parametros);
                if (reflab.isEmpty()) {
                    laboral = new Verificacionesreferencias();
                    laboral.setExperiencia(experiencia);
                    laboral.setActivo(Boolean.TRUE);
                    laboral.setFecha(fecha);
                } else {
                    for (Verificacionesreferencias valor : reflab) {
                        //laboral.equals(valor);
                        laboral = valor;
                        fecha = laboral.getFecha();
                    }
                }
            } catch (ConsultarException ex) {
                Logger.getLogger(VerificacionReferenciasBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (listaVDesempenio.isEmpty()) {
                for (Valoracionesdesempenio vn : listaValoraciones) {
                    Verificacionesdesempenio n = new Verificacionesdesempenio();
                    n.setExperiencia(experiencia);
                    n.setActivo(Boolean.TRUE);
                    n.setValoracion(vn);
                    listaVDesempenio.add(n);
                }
            }
        }
        formularioEx.insertar();
        return null;
    }

    public boolean validarExperiencias() {
        if (laboral.getCausadesvinculacion() == null || laboral.getCausadesvinculacion().isEmpty()) {
            MensajesErrores.advertencia("Indicar 'Causas de desvinculación'");
            return true;
        }
        if (laboral.getRecomiendacargo() == null || laboral.getRecomiendacargo().isEmpty()) {
            MensajesErrores.advertencia("Indicar 'Si lo recomendaría para el cargo'");
            return true;
        }
        if (laboral.getPuntosdesarrollar() == null || laboral.getPuntosdesarrollar().isEmpty()) {
            MensajesErrores.advertencia("Indicar 'Puntos a desarrollar'");
            return true;
        }
        if (laboral.getObservacion() == null || laboral.getObservacion().isEmpty()) {
            MensajesErrores.advertencia("Indicar 'Observaciones'");
            return true;
        }
        return false;
    }

    public String grabarExperiencias() {
        if (validarExperiencias()) {
            return null;
        }
        for (Verificacionesdesempenio cv : listaVDesempenio) {
            if (cv.getValor() != null) {
                int ref = 0;
                int refMin = 0;
                int refMax = 0;
                ref = cv.getValor().compareTo(BigDecimal.ZERO);
                refMin = cv.getValor().compareTo(cv.getValoracion().getMinimo());
                refMax = cv.getValor().compareTo(cv.getValoracion().getMaximo());

                if (ref == 0) {
                    MensajesErrores.advertencia("Existe un registro de puntaje von valor '0' ");
                    return null;
                }
                if (refMax == 1) {
                    MensajesErrores.advertencia("Existen un registro de puntaje superior al máximo");
                    return null;
                }
                if (refMin == -1) {
                    MensajesErrores.advertencia("Existen un registro de puntaje inferior al mínimo");
                    return null;
                }
            } else {
                MensajesErrores.advertencia("Existen un registro de puntaje nulo o vacio");
                return null;
            }
        }
        try {
            for (Verificacionesdesempenio vd : listaVDesempenio) {
                if (vd.getId() == null) {
                    ejbVDesempenio.create(vd, parametrosSeguridad.getLogueado().getUserid());
                } else {
                    ejbVDesempenio.edit(vd, parametrosSeguridad.getLogueado().getUserid());
                }
            }
            if (laboral.getId() == null) {
                ejbVReferencias.create(laboral, parametrosSeguridad.getLogueado().getUserid());
            } else {
                ejbVReferencias.edit(laboral, parametrosSeguridad.getLogueado().getUserid());
            }
            experiencia.setResponvalid(laboral.getObservacion());
            experiencia.setResponvalid(parametrosSeguridad.getLogueado().getUserid());
            ejbExperiencias.edit(experiencia, parametrosSeguridad.getLogueado().getUserid());
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + ";" + ex.getCause());
            Logger.getLogger(VerificacionReferenciasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Registro guardado");
        formularioEx.cancelar();
        return null;
    }

//HABILIDADES
    public String verificarHabilidades(Habilidades hab, int index) {
        habilidad = hab;
        formularioH.setIndiceFila(index);
        formularioH.insertar();
        return null;
    }

    public String grabarHabilidades() {
        try {
            habilidad.setResponvalid(parametrosSeguridad.getLogueado().getUserid());
            ejbHabilidades.edit(habilidad, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            Logger.getLogger(ValidarEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Registro guardado");
        formularioH.cancelar();
        return null;
    }
//RECOMENDACIONES PERSONALES

    public String verificarPersonales(Recomendaciones rp, int index) {
        recomendacion = rp;
        formularioP.setIndiceFila(index);
        fecha = new Date();
        listaVReferencias = new LinkedList<>();
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.recomendacion=:recomendacion and o.activo=true");
            parametros.put("recomendacion", recomendacion);
            List<Verificacionesreferencias> refper = ejbVReferencias.encontarParametros(parametros);
            if (refper.isEmpty()) {
                personal = new Verificacionesreferencias();
                personal.setRecomendacion(recomendacion);
                personal.setActivo(Boolean.TRUE);
                personal.setFecha(fecha);
            } else {
                for (Verificacionesreferencias valor : refper) {
                    personal = valor;
                    fecha = personal.getFecha();
                    //personal.equals(valor);
                }
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(VerificacionReferenciasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioP.insertar();
        return null;
    }

    public boolean validarPersonales() {
        if (personal.getDescripcioncomportamiento() == null || personal.getDescripcioncomportamiento().isEmpty()) {
            MensajesErrores.advertencia("Indicar 'Descripción de Comportamiento'");
            return true;
        }
        if (personal.getObservacion() == null || personal.getObservacion().isEmpty()) {
            MensajesErrores.advertencia("Indicar 'Observaciones'");
            return true;
        }
        return false;
    }

    public String grabarPersonales() {
        if (validarPersonales()) {
            return null;
        }
        try {
            if (personal.getId() == null) {
                ejbVReferencias.create(personal, parametrosSeguridad.getLogueado().getUserid());
            } else {
                ejbVReferencias.edit(personal, parametrosSeguridad.getLogueado().getUserid());
            }
            recomendacion.setObsvalidado(personal.getObservacion());
            recomendacion.setResponvalid(parametrosSeguridad.getLogueado().getUserid());
            ejbRecomendaciones.edit(recomendacion, parametrosSeguridad.getLogueado().getUserid());
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + ";" + ex.getCause());
            Logger.getLogger(VerificacionReferenciasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Registro guardado");
        formularioP.cancelar();
        return null;
    }

    public SelectItem[] getComboCargosSolicitudes() {
        try {
            List<Cargos> listaCargos = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";orden", "o.id");
            parametros.put(";where", "o.activo=true");
            List<Cargos> aux;
            aux = ejbCargos.encontarParametros(parametros);
            if (!aux.isEmpty()) {
                for (Cargos cargo : aux) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.activo=true and o.cargoactual=:cargo");
                    parametros.put("cargo", cargo);
                    List<Empleados> empleados = ejbEmpleados.encontarParametros(parametros);
                    if (!empleados.isEmpty()) {
                        for (Empleados em : empleados) {
//                            listaCargos.add(em.getCargoactual());
                        }
                    }
                }
            }
            return Combos.getSelectItems(listaCargos, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ValidarEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String cerrarVerificacion() {
        return "/sistema/BlancoVista.jsf?faces-redirect=true";
    }

    public String verDocumento(byte[] doc) {
        Calendar c = Calendar.getInstance();
        setNombrearchivo(c.getTimeInMillis() + "datos");
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        setImagenrs(new Recurso(doc));
        getFormularioImagen().insertar();
        return null;
    }

    public SelectItem[] getValores(Verificacionesdesempenio v) {
        notaEx = v.getValor().intValue();
        int contador = 0;
        int inicio = 0;
        int fin = 0;
        listaValores = new LinkedList<>();
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.activo=true and  o.id=:id");
            parametros.put("id", v.getValoracion().getId());
            List<Valoracionesdesempenio> aux;
            aux = ejbValoraciones.encontarParametros(parametros);
            for (Valoracionesdesempenio vd : aux) {
                inicio = vd.getMinimo().intValue();
                fin = vd.getMaximo().intValue();
                //contador = inicio;
                while (fin > contador) {
                    contador++;
                    listaValores.add(contador);
                }
            }
            v.setNota(v.getValor());
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(VerificacionReferenciasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Combos.getSelectItems(listaValores, true);
    }


    public String getRangoValores(Verificacionesdesempenio v) {

        String min = " ";
        String max = " ";
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.activo=true  and o.id=:id");
            parametros.put("id", v.getValoracion().getId());
            List<Valoracionesdesempenio> aux;
            aux = ejbValoraciones.encontarParametros(parametros);
            if (!aux.isEmpty()) {
                min = aux.get(0).getMinimo() + "";
                max = aux.get(0).getMaximo() + "";
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(VerificacionReferenciasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return min + " - " + max;
    }

 

    /**
     * @return the listaEmpleados
     */
    public LazyDataModel<Empleados> getListaEmpleados() {
        return listaEmpleados;
    }

    /**
     * @param listaEmpleados the listaEmpleados to set
     */
    public void setListaEmpleados(LazyDataModel<Empleados> listaEmpleados) {
        this.listaEmpleados = listaEmpleados;
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
     * @return the listaCursos
     */
    public List<Cursos> getListaCursos() {
        return listaCursos;
    }

    /**
     * @param listaCursos the listaCursos to set
     */
    public void setListaCursos(List<Cursos> listaCursos) {
        this.listaCursos = listaCursos;
    }

    /**
     * @return the curso
     */
    public Cursos getCurso() {
        return curso;
    }

    /**
     * @param curso the curso to set
     */
    public void setCurso(Cursos curso) {
        this.curso = curso;
    }

    /**
     * @return the formularioC
     */
    public Formulario getFormularioC() {
        return formularioC;
    }

    /**
     * @param formularioC the formularioC to set
     */
    public void setFormularioC(Formulario formularioC) {
        this.formularioC = formularioC;
    }

    /**
     * @return the listaEstudios
     */
    public List<Estudios> getListaEstudios() {
        return listaEstudios;
    }

    /**
     * @param listaEstudios the listaEstudios to set
     */
    public void setListaEstudios(List<Estudios> listaEstudios) {
        this.listaEstudios = listaEstudios;
    }

    /**
     * @return the estudio
     */
    public Estudios getEstudio() {
        return estudio;
    }

    /**
     * @param estudio the estudio to set
     */
    public void setEstudio(Estudios estudio) {
        this.estudio = estudio;
    }

    /**
     * @return the formularioE
     */
    public Formulario getFormularioE() {
        return formularioE;
    }

    /**
     * @param formularioE the formularioE to set
     */
    public void setFormularioE(Formulario formularioE) {
        this.formularioE = formularioE;
    }

    /**
     * @return the listaIdiomas
     */
    public List<Idiomas> getListaIdiomas() {
        return listaIdiomas;
    }

    /**
     * @param listaIdiomas the listaIdiomas to set
     */
    public void setListaIdiomas(List<Idiomas> listaIdiomas) {
        this.listaIdiomas = listaIdiomas;
    }

    /**
     * @return the idioma
     */
    public Idiomas getIdioma() {
        return idioma;
    }

    /**
     * @param idioma the idioma to set
     */
    public void setIdioma(Idiomas idioma) {
        this.idioma = idioma;
    }

    /**
     * @return the formularioI
     */
    public Formulario getFormularioI() {
        return formularioI;
    }

    /**
     * @param formularioI the formularioI to set
     */
    public void setFormularioI(Formulario formularioI) {
        this.formularioI = formularioI;
    }

    /**
     * @return the listaExperiencias
     */
    public List<Experiencias> getListaExperiencias() {
        return listaExperiencias;
    }

    /**
     * @param listaExperiencias the listaExperiencias to set
     */
    public void setListaExperiencias(List<Experiencias> listaExperiencias) {
        this.listaExperiencias = listaExperiencias;
    }

    /**
     * @return the experiencia
     */
    public Experiencias getExperiencia() {
        return experiencia;
    }

    /**
     * @param experiencia the experiencia to set
     */
    public void setExperiencia(Experiencias experiencia) {
        this.experiencia = experiencia;
    }

    /**
     * @return the formularioEx
     */
    public Formulario getFormularioEx() {
        return formularioEx;
    }

    /**
     * @param formularioEx the formularioEx to set
     */
    public void setFormularioEx(Formulario formularioEx) {
        this.formularioEx = formularioEx;
    }

    /**
     * @return the listaHabilidades
     */
    public List<Habilidades> getListaHabilidades() {
        return listaHabilidades;
    }

    /**
     * @param listaHabilidades the listaHabilidades to set
     */
    public void setListaHabilidades(List<Habilidades> listaHabilidades) {
        this.listaHabilidades = listaHabilidades;
    }

    /**
     * @return the formularioH
     */
    public Formulario getFormularioH() {
        return formularioH;
    }

    /**
     * @param formularioH the formularioH to set
     */
    public void setFormularioH(Formulario formularioH) {
        this.formularioH = formularioH;
    }

    /**
     * @return the cargo
     */
    public Cargos getCargo() {
        return cargo;
    }

    /**
     * @param cargo the cargo to set
     */
    public void setCargo(Cargos cargo) {
        this.cargo = cargo;
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
     * @return the habilidad
     */
    public Habilidades getHabilidad() {
        return habilidad;
    }

    /**
     * @param habilidad the habilidad to set
     */
    public void setHabilidad(Habilidades habilidad) {
        this.habilidad = habilidad;
    }

    /**
     * @return the listaPersonales
     */
    public List<Recomendaciones> getListaPersonales() {
        return listaPersonales;
    }

    /**
     * @param listaPersonales the listaPersonales to set
     */
    public void setListaPersonales(List<Recomendaciones> listaPersonales) {
        this.listaPersonales = listaPersonales;
    }

    /**
     * @return the formularioP
     */
    public Formulario getFormularioP() {
        return formularioP;
    }

    /**
     * @param formularioP the formularioP to set
     */
    public void setFormularioP(Formulario formularioP) {
        this.formularioP = formularioP;
    }

    /**
     * @return the Nombrearchivo
     */
    public String getNombrearchivo() {
        return Nombrearchivo;
    }

    /**
     * @param Nombrearchivo the Nombrearchivo to set
     */
    public void setNombrearchivo(String Nombrearchivo) {
        this.Nombrearchivo = Nombrearchivo;
    }

    /**
     * @return the imagenrs
     */
    public Resource getImagenrs() {
        return imagenrs;
    }

    /**
     * @param imagenrs the imagenrs to set
     */
    public void setImagenrs(Resource imagenrs) {
        this.imagenrs = imagenrs;
    }

    /**
     * @return the formularioImagen
     */
    public Formulario getFormularioImagen() {
        return formularioImagen;
    }

    /**
     * @param formularioImagen the formularioImagen to set
     */
    public void setFormularioImagen(Formulario formularioImagen) {
        this.formularioImagen = formularioImagen;
    }

    /**
     * @return the laboral
     */
    public Verificacionesreferencias getLaboral() {
        return laboral;
    }

    /**
     * @param laboral the laboral to set
     */
    public void setLaboral(Verificacionesreferencias laboral) {
        this.laboral = laboral;
    }

    /**
     * @return the listaVReferencias
     */
    public List<Verificacionesreferencias> getListaVReferencias() {
        return listaVReferencias;
    }

    /**
     * @param listaVReferencias the listaVReferencias to set
     */
    public void setListaVReferencias(List<Verificacionesreferencias> listaVReferencias) {
        this.listaVReferencias = listaVReferencias;
    }

    /**
     * @return the listaVLaborales
     */
    public List<Verificacionesreferencias> getListaVLaborales() {
        return listaVLaborales;
    }

    /**
     * @param listaVLaborales the listaVLaborales to set
     */
    public void setListaVLaborales(List<Verificacionesreferencias> listaVLaborales) {
        this.listaVLaborales = listaVLaborales;
    }

    /**
     * @return the listaVDesempenio
     */
    public List<Verificacionesdesempenio> getListaVDesempenio() {
        return listaVDesempenio;
    }

    /**
     * @param listaVDesempenio the listaVDesempenio to set
     */
    public void setListaVDesempenio(List<Verificacionesdesempenio> listaVDesempenio) {
        this.listaVDesempenio = listaVDesempenio;
    }

    /**
     * @return the desempenio
     */
    public Verificacionesdesempenio getDesempenio() {
        return desempenio;
    }

    /**
     * @param desempenio the desempenio to set
     */
    public void setDesempenio(Verificacionesdesempenio desempenio) {
        this.desempenio = desempenio;
    }

    /**
     * @return the listaValoraciones
     */
    public List<Valoracionesdesempenio> getListaValoraciones() {
        return listaValoraciones;
    }

    /**
     * @param listaValoraciones the listaValoraciones to set
     */
    public void setListaValoraciones(List<Valoracionesdesempenio> listaValoraciones) {
        this.listaValoraciones = listaValoraciones;
    }

    /**
     * @return the fecha
     */
    public Date getFecha() {
        return fecha;
    }

    /**
     * @param fecha the fecha to set
     */
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    /**
     * @return the recomendacion
     */
    public Recomendaciones getRecomendacion() {
        return recomendacion;
    }

    /**
     * @param recomendacion the recomendacion to set
     */
    public void setRecomendacion(Recomendaciones recomendacion) {
        this.recomendacion = recomendacion;
    }

    /**
     * @return the personal
     */
    public Verificacionesreferencias getPersonal() {
        return personal;
    }

    /**
     * @param personal the personal to set
     */
    public void setPersonal(Verificacionesreferencias personal) {
        this.personal = personal;
    }

    /**
     * @return the listaValores
     */
    public List<Integer> getListaValores() {
        return listaValores;
    }

    /**
     * @param listaValores the listaValores to set
     */
    public void setListaValores(List<Integer> listaValores) {
        this.listaValores = listaValores;
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
     * @return the listaNotasExp
     */
    public List<BigDecimal> getListaNotasExp() {
        return listaNotasExp;
    }

    /**
     * @param listaNotasExp the listaNotasExp to set
     */
    public void setListaNotasExp(List<BigDecimal> listaNotasExp) {
        this.listaNotasExp = listaNotasExp;
    }

   
}
