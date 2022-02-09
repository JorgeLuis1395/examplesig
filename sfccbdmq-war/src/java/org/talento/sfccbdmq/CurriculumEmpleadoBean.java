/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

import com.ejb.sfcarchivos.ArchivosFacade;
import com.entidades.sfcarchivos.Archivos;
import com.entidades.sfcarchivos.Imagenes;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.Serializable;
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
import org.beans.sfccbdmq.CursosFacade;
import org.beans.sfccbdmq.EstudiosFacade;
import org.beans.sfccbdmq.ExperienciasFacade;
import org.beans.sfccbdmq.FamiliasFacade;
import org.beans.sfccbdmq.HabilidadesFacade;
import org.beans.sfccbdmq.HistorialcargosFacade;
import org.beans.sfccbdmq.HistorialsancionesFacade;
import org.beans.sfccbdmq.IdiomasFacade;
import org.beans.sfccbdmq.RecomendacionesFacade;
import org.entidades.sfccbdmq.Cursos;
import org.entidades.sfccbdmq.Estudios;
import org.entidades.sfccbdmq.Experiencias;
import org.entidades.sfccbdmq.Familias;
import org.entidades.sfccbdmq.Habilidades;
import org.entidades.sfccbdmq.Historialcargos;
import org.entidades.sfccbdmq.Historialsanciones;
import org.entidades.sfccbdmq.Idiomas;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Recomendaciones;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "curriculumEmpleado")
@ViewScoped
public class CurriculumEmpleadoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    private List<Cursos> listaCursos;
    private List<Familias> listaFamilias;
    private List<Experiencias> listaExperiencias;
    private List<Estudios> listaEstudios;
    private List<Habilidades> listaHabilidades;
    private List<Idiomas> listaIdiomas;
    private List<Recomendaciones> listaRecomendaciones;
    private List<Historialcargos> listaHistotialCargos;
    private Cursos curso;
    private Formulario formulario = new Formulario();
    private Formulario formularioPrincipal = new Formulario();
    private Archivos archivoImagen;
    private Imagenes imagen;
    @EJB
    private CursosFacade ejbCursos;
    @EJB
    private FamiliasFacade ejbFamilias;
    @EJB
    private EstudiosFacade ejbEstudios;
    @EJB
    private ExperienciasFacade ejbExperiencias;
    @EJB
    private HabilidadesFacade ejbHabilidades;
    @EJB
    private IdiomasFacade ejbIdiomas;
    @EJB
    private RecomendacionesFacade ejbRecomendaciones;
    @EJB
    private HistorialcargosFacade ejbHistorial;
    @EJB
    private HistorialsancionesFacade ejbHistorialsanciones;
    @EJB
    private ArchivosFacade ejbArchivos;
    private List<Historialsanciones> listaHistorial;
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
        String nombreForma = "CurriculumEmpleadosVista";
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
    /**
     * Creates a new instance of CursosEmpleadoBean
     */
    public CurriculumEmpleadoBean() {
    }

    
    public String buscar() {

        if (empleadoBean.getEmpleadoSeleccionado() == null) {
            MensajesErrores.advertencia("Ingrese un empleado");
            return null;
        }

        try {
            empleadoBean.setEmpleado(empleadoBean.getEmpleadoSeleccionado());
            empleadoBean.modificarSeleccionado();
            Map parametros = new HashMap();
            String where = "o.empleado=:empleado and o.empleado.activo=true ";
            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
            parametros.put(";where", where);
//            parametros.put(";orden", "o.fecha desc");
            listaCursos = ejbCursos.encontarParametros(parametros);
            listaEstudios = ejbEstudios.encontarParametros(parametros);
            listaExperiencias = ejbExperiencias.encontarParametros(parametros);
//            listaFamilias = ejbFamilias.encontarParametros(parametros);
            listaHabilidades = ejbHabilidades.encontarParametros(parametros);
            listaIdiomas = ejbIdiomas.encontarParametros(parametros);
            listaRecomendaciones = ejbRecomendaciones.encontarParametros(parametros);

            parametros = new HashMap();
            where = "o.entidad=:empleado";
            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado().getEntidad());
            parametros.put(";where", where);
//            parametros.put(";orden", "o.fecha desc");
            listaFamilias = ejbFamilias.encontarParametros(parametros);
            parametros = new HashMap();
            where = "o.empleado=:empleado";
            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
            parametros.put(";where", where);
            parametros.put(";orden", "o.fecha desc");
            listaHistotialCargos = ejbHistorial.encontarParametros(parametros);
            parametros = new HashMap();
            where = "o.empleado=:empleado ";
            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
            parametros.put(";where", where);
            parametros.put(";orden", "o.fsancion desc");
            listaHistorial = ejbHistorialsanciones.encontarParametros(parametros);
            // taer la foto
            parametros = new HashMap();
            parametros.put(";where", "o.imagen.modulo='EMPLEADOFOTO' and o.imagen.idmodulo=:activo");
            parametros.put("activo", empleadoBean.getEmpleadoSeleccionado().getEntidad().getId());
            setArchivoImagen(null);
            setImagen(null);
            
            List<Archivos> larch = ejbArchivos.encontarParametros(parametros);
            for (Archivos a : larch) {
                setArchivoImagen(a);
                setImagen(a.getImagen());
            }
            formulario.insertar();
        } catch (ConsultarException ex) {
            MensajesErrores.advertencia(ex.getMessage());
            Logger.getLogger(CurriculumEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the empleadoBean
     */
    public EmpleadosBean getEmpleadoBean() {
        return empleadoBean;
    }

    /**
     * @param empleadoBean the empleadoBean to set
     */
    public void setEmpleadoBean(EmpleadosBean empleadoBean) {
        this.empleadoBean = empleadoBean;
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
     * @return the imagenesBean
     */
    public ImagenesBean getImagenesBean() {
        return imagenesBean;
    }

    /**
     * @param imagenesBean the imagenesBean to set
     */
    public void setImagenesBean(ImagenesBean imagenesBean) {
        this.imagenesBean = imagenesBean;
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
     * @return the listaFamilias
     */
    public List<Familias> getListaFamilias() {
        return listaFamilias;
    }

    /**
     * @param listaFamilias the listaFamilias to set
     */
    public void setListaFamilias(List<Familias> listaFamilias) {
        this.listaFamilias = listaFamilias;
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
     * @return the listaRecomendaciones
     */
    public List<Recomendaciones> getListaRecomendaciones() {
        return listaRecomendaciones;
    }

    /**
     * @param listaRecomendaciones the listaRecomendaciones to set
     */
    public void setListaRecomendaciones(List<Recomendaciones> listaRecomendaciones) {
        this.listaRecomendaciones = listaRecomendaciones;
    }

    /**
     * @return the listaHistorial
     */
    public List<Historialsanciones> getListaHistorial() {
        return listaHistorial;
    }

    /**
     * @param listaHistorial the listaHistorial to set
     */
    public void setListaHistorial(List<Historialsanciones> listaHistorial) {
        this.listaHistorial = listaHistorial;
    }

    /**
     * @return the listaHistotialCargos
     */
    public List<Historialcargos> getListaHistotialCargos() {
        return listaHistotialCargos;
    }

    /**
     * @param listaHistotialCargos the listaHistotialCargos to set
     */
    public void setListaHistotialCargos(List<Historialcargos> listaHistotialCargos) {
        this.listaHistotialCargos = listaHistotialCargos;
    }

    public String cargaListner() {
        buscar();
        formularioPrincipal.insertar();
        return null;
    }

    /**
     * @return the formularioPrincipal
     */
    public Formulario getFormularioPrincipal() {
        return formularioPrincipal;
    }

    /**
     * @param formularioPrincipal the formularioPrincipal to set
     */
    public void setFormularioPrincipal(Formulario formularioPrincipal) {
        this.formularioPrincipal = formularioPrincipal;
    }

    /**
     * @return the archivoImagen
     */
    public Archivos getArchivoImagen() {
        return archivoImagen;
    }

    /**
     * @param archivoImagen the archivoImagen to set
     */
    public void setArchivoImagen(Archivos archivoImagen) {
        this.archivoImagen = archivoImagen;
    }

    /**
     * @return the imagen
     */
    public Imagenes getImagen() {
        return imagen;
    }

    /**
     * @param imagen the imagen to set
     */
    public void setImagen(Imagenes imagen) {
        this.imagen = imagen;
    }
}
