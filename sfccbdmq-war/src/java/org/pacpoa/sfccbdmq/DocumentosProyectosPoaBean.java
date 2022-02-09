/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pacpoa.sfccbdmq;

import org.beans.sfccbdmq.ProyectospoaFacade;
import org.beans.sfccbdmq.TrackingspoaFacade;
import org.entidades.sfccbdmq.Documentospoa;
import org.entidades.sfccbdmq.Proyectospoa;
import org.entidades.sfccbdmq.Trackingspoa;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
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
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesPoaBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author edison
 */
@ManagedBean(name = "documentosProyectoPoa")
@ViewScoped
public class DocumentosProyectosPoaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{imagenesPacpoa}")
    private ImagenesPoaBean imagenesPoaBean;
    private Formulario formulario = new Formulario();
    private Formulario formularioAprobacion = new Formulario();
    private Proyectospoa programa;
    private Proyectospoa proyecto;
    private LazyDataModel<Proyectospoa> listadoProyectos;
    private String codigo;
    private String nombre;
    private String superior;
    private boolean controlTitulo = Boolean.TRUE;
    private String direccion;
    private String coddireccion;
    private Proyectospoa anterior;
    private Proyectospoa actividad;
    private List<Documentospoa> listaDocumentos;

    @EJB
    private ProyectospoaFacade ejbProyectospoa;

    @EJB
    private TrackingspoaFacade ejbTrackingspoa;

    @PostConstruct
    public void idenficarDireccion() {
//        if (!ejbLogin.traerDireccionEmpleado(seguridadbean.getEntidad().getPin()).getCodigo().isEmpty()) {
//            coddireccion = ejbLogin.traerDireccionEmpleado(seguridadbean.getEntidad().getPin()).getCodigo();
//        }
//        coddireccion = null;
    }

    public DocumentosProyectosPoaBean() {
        listadoProyectos = new LazyDataModel<Proyectospoa>() {
            @Override
            public List<Proyectospoa> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return carga(i, i1, scs, map);
            }
        };
    }

    public List<Proyectospoa> carga(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
        if (programa == null) {
            MensajesErrores.advertencia("Requiere de seleccionar un Programa");
            return null;
        }
        try {
            Map parametros = new HashMap();
            String where = "o.activo=true and o.padre=:programa ";
            parametros.put("programa", programa);
            parametros.put(";orden", "o.codigo asc, o.padre.id asc");
            //PROGRAMA
//            if (programa != null) {
//                where += " and upper(o.codigo) like :programa";
//                parametros.put("programa", programa.getCodigo().toUpperCase() + "%");
//            }
            //CODIGO
            if (!(codigo == null || codigo.isEmpty())) {
                where += " and upper(o.codigo) like :codigo";
                parametros.put("codigo", codigo.toUpperCase() + "%");
            }
            //NOMBRE
            if (!(nombre == null || nombre.isEmpty())) {
                where += " and upper(o.nombre) like :nombre";
                parametros.put("nombre", "%" + nombre.toUpperCase() + "%");
            }
            parametros.put(";where", where);
            List<Proyectospoa> aux = ejbProyectospoa.encontarParametros(parametros);
            int total = ejbProyectospoa.contar(parametros);
            int endIndex = i + pageSize;
            if (endIndex > total) {
                endIndex = total;
            }
            parametros.put(";inicial", i);
            parametros.put(";final", endIndex);
            listadoProyectos.setRowCount(total);
            return ejbProyectospoa.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(DocumentosProyectosPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String buscar() {
        if (programa == null) {
            MensajesErrores.advertencia("Requiere de seleccionar un Programa");
            return null;
        }
        listadoProyectos = new LazyDataModel<Proyectospoa>() {
            @Override
            public List<Proyectospoa> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return carga(i, i1, scs, map);
            }
        };
        return null;
    }

//    public String modificar() {
//        listaDocumentos = new LinkedList<>();
//        try {
//            proyecto = (Proyectos) listadoProyectos.getRowData();
//            controlTitulo = proyecto.getDetitulo();
//            Map parametros = new HashMap();
//            parametros.put(";where", "o.activo=true and o.proyecto.padre=:proyecto");
//            parametros.put("proyecto", proyecto);
//            listaSubactividades = ejbSubactividad.encontarParametros(parametros);
//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
//            Logger.getLogger(DocumentosProyectosBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        formulario.editar();
//        return null;
//    }
    public String grabar() {

        try {
//            proyecto.setFecha(new Date());
//            proyecto.setResponsable(seguridadbean.getEntidad().getApellidos() + " " + seguridadbean.getEntidad().getNombres());
            ejbProyectospoa.edit(proyecto, seguridadbean.getLogueado().getUserid());

            Trackingspoa tracking = new Trackingspoa();
            tracking.setReformanombre(false);
            tracking.setFecha(new Date());
            tracking.setProyecto(proyecto);
//            tracking.setEstado(String.valueOf(proyecto.getEstado()));
//            tracking.setObservaciones("Proyecto: " + proyecto.getNombre() + (!proyecto.getAlineado() ? "NO" : "") + " ALINEADO con fecha : " + format1.format(proyecto.getFecha()) + " por : " + seguridadbean.getEntidad().getApellidos() + " " + seguridadbean.getEntidad().getNombres());
            ejbTrackingspoa.create(tracking, seguridadbean.getLogueado().getUserid());

        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(ProyectosPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        } 
        buscar();
        formulario.cancelar();
        return null;
    }

//    public double totalPresupuesto(Proyectos p) {
//        double suma = 0;
//        try {
//            Map parametros = new HashMap();
//            parametros.put(";where", "o.activo=true and o.proyecto=:proyecto");
//            parametros.put("proyecto", p);
//            List<Subactividades> aux;
//            aux = ejbSubactividad.encontarParametros(parametros);
//            if (aux.isEmpty()) {
//                parametros = new HashMap();
//                parametros.put(";where", "o.padre=:padre");
//                parametros.put("padre", p);
//                List<Proyectos> listaActividades = ejbProyectos.encontarParametros(parametros);
//                for (Proyectos py : listaActividades) {
//                    suma += totalPresupuesto(py);
//                }
//            }
//            for (Object as : aux) {
//                Subactividades apsi = (Subactividades) as;
//                if (apsi != null && apsi.getValor() != null) {
//                    suma += apsi.getValor().doubleValue();
//                }
//            }
//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
//            Logger.getLogger(DocumentosProyectosBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return suma;
//    }
//    public Subactividades subactividadFinal(Proyectos p) {
//        try {
//            Map parametros = new HashMap();
//            parametros.put(";where", "o.activo=true and upper(o.codigo) like :codigo");
//            parametros.put("codigo", p.getCodigo().toUpperCase() + "%");
//            parametros.put(";orden", "o.fechafin desc");
//            List<Subactividades> aux = ejbSubactividad.encontarParametros(parametros);
//            if (!aux.isEmpty()) {
//                return aux.get(0);
//            }
//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
//            Logger.getLogger(DocumentosProyectosBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }
    public List<Proyectospoa> listaProyectos(int nivel) {
        List<Proyectospoa> retorno = new LinkedList<>();
        try {
            Map parametros = new HashMap();
            parametros.put("padre", proyecto);
            switch (nivel) {
                case 1:
                    retorno.add(programa);
                    break;
                case 2:
                    retorno.add(proyecto);
                    break;
                case 3:
                    parametros.put(";where", "o.padre=:padre and o.activo = true");
                    retorno = ejbProyectospoa.encontarParametros(parametros);
                    break;
            }

        } catch (ConsultarException ex) {
            Logger.getLogger(DocumentosProyectosPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

//    public List<Subactividades> listaSubactividades() {
//        List<Proyectos> proyectos = listaProyectos(3);
//        List<Subactividades> retorno = new LinkedList<>();
//        try {
//            for (Proyectos p : proyectos) {
//                Map parametros = new HashMap();
//                parametros.put(";where", "o.proyecto=:proyecto and o.activo = true");
//                parametros.put("proyecto", p);
//                List<Subactividades> aux = ejbSubactividad.encontarParametros(parametros);
//                for (Subactividades s : aux) {
//                    retorno.add(s);
//                }
//
//            }
//
//        } catch (ConsultarException ex) {
//            Logger.getLogger(DocumentosProyectosBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return retorno;
//    }
    public String aprobarProyectos() {
        if (programa == null) {
            MensajesErrores.advertencia("Requiere de seleccionar un Programa");
            return null;
        }

        formularioAprobacion.insertar();
        return null;
    }

    public String grabarAprobacionProyectos() {

        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.padre=:programa and (o.alineado is null or o.alineado = false) and o.activo=true");
            parametros.put("programa", programa);

            if (ejbProyectospoa.contar(parametros) > 0) {
                MensajesErrores.advertencia("No se puede continuar porque los proyectos no están alineados");
                return null;
            }
            parametros = new HashMap();
            parametros.put(";where", "o.padre=:programa and o.alineado = true and o.activo=true");
            parametros.put("programa", programa);

            List<Proyectospoa> lista = ejbProyectospoa.encontarParametros(parametros);

            for (Proyectospoa p : lista) {
                p.setDefinitivo(true);
//                p.setEstado(2);//Aprobado
                ejbProyectospoa.edit(p, seguridadbean.getLogueado().getUserid());

                Trackingspoa tracking = new Trackingspoa();
                tracking.setReformanombre(false);
                SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy'/'HH:mm:ss");
                tracking.setFecha(new Date());
                tracking.setProyecto(p);
//                tracking.setEstado(String.valueOf(p.getEstado()));
                tracking.setObservaciones("Proyecto: " + p.getNombre() + " APROBADO con fecha : " + format1.format(new Date()) + " por : " + seguridadbean.getLogueado().getApellidos() + " " + seguridadbean.getLogueado().getNombres());
                tracking.setResponsable(seguridadbean.getLogueado().getApellidos() + " " + seguridadbean.getLogueado().getNombres());
                ejbTrackingspoa.create(tracking, seguridadbean.getLogueado().getUserid());

            }
            buscar();
            formularioAprobacion.cancelar();
        } catch (GrabarException | InsertarException | ConsultarException ex) {
            //} catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(DocumentosProyectosPoaBean.class.getName()).log(Level.SEVERE, null, ex);
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
     * @return the programa
     */
    public Proyectospoa getPrograma() {
        return programa;
    }

    /**
     * @param programa the programa to set
     */
    public void setPrograma(Proyectospoa programa) {
        this.programa = programa;
    }

    /**
     * @return the proyecto
     */
    public Proyectospoa getProyecto() {
        return proyecto;
    }

    /**
     * @param proyecto the proyecto to set
     */
    public void setProyecto(Proyectospoa proyecto) {
        this.proyecto = proyecto;
    }

    /**
     * @return the listadoProyectos
     */
    public LazyDataModel<Proyectospoa> getListadoProyectos() {
        return listadoProyectos;
    }

    /**
     * @param listadoProyectos the listadoProyectos to set
     */
    public void setListadoProyectos(LazyDataModel<Proyectospoa> listadoProyectos) {
        this.listadoProyectos = listadoProyectos;
    }

    /**
     * @return the codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
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
     * @return the superior
     */
    public String getSuperior() {
        return superior;
    }

    /**
     * @param superior the superior to set
     */
    public void setSuperior(String superior) {
        this.superior = superior;
    }

    /**
     * @return the controlTitulo
     */
    public boolean isControlTitulo() {
        return controlTitulo;
    }

    /**
     * @param controlTitulo the controlTitulo to set
     */
    public void setControlTitulo(boolean controlTitulo) {
        this.controlTitulo = controlTitulo;
    }

    /**
     * @return the direccion
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * @param direccion the direccion to set
     */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    /**
     * @return the coddireccion
     */
    public String getCoddireccion() {
        return coddireccion;
    }

    /**
     * @param coddireccion the coddireccion to set
     */
    public void setCoddireccion(String coddireccion) {
        this.coddireccion = coddireccion;
    }

    /**
     * @return the anterior
     */
    public Proyectospoa getAnterior() {
        return anterior;
    }

    /**
     * @param anterior the anterior to set
     */
    public void setAnterior(Proyectospoa anterior) {
        this.anterior = anterior;
    }

    /**
     * @return the actividad
     */
    public Proyectospoa getActividad() {
        return actividad;
    }

    /**
     * @param actividad the actividad to set
     */
    public void setActividad(Proyectospoa actividad) {
        this.actividad = actividad;
    }

    /**
     * @return the imagenesBean
     */
    public ImagenesPoaBean getImagenesPoaBean() {
        return imagenesPoaBean;
    }

    /**
     * @param imagenesBean the imagenesBean to set
     */
    public void setImagenesPoaBean(ImagenesPoaBean imagenesBean) {
        this.imagenesPoaBean = imagenesBean;
    }

    /**
     * @return the listaDocumentos
     */
    public List<Documentospoa> getListaDocumentospoa() {
        return listaDocumentos;
    }

    /**
     * @param listaDocumentos the listaDocumentos to set
     */
    public void setListaDocumentos(List<Documentospoa> listaDocumentos) {
        this.listaDocumentos = listaDocumentos;
    }

    /**
     * @return the formularioAprobacion
     */
    public Formulario getFormularioAprobacion() {
        return formularioAprobacion;
    }

    /**
     * @param formularioAprobacion the formularioAprobacion to set
     */
    public void setFormularioAprobacion(Formulario formularioAprobacion) {
        this.formularioAprobacion = formularioAprobacion;
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
     * @return the listaDocumentos
     */
    public List<Documentospoa> getListaDocumentos() {
        return listaDocumentos;
    }

}
