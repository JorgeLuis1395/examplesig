/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pacpoa.sfccbdmq;

import org.beans.sfccbdmq.AsignacionespoaFacade;
import org.beans.sfccbdmq.DetallecertificacionespoaFacade;
import org.beans.sfccbdmq.ProyectospoaFacade;
import org.beans.sfccbdmq.ReformaspoaFacade;
import org.beans.sfccbdmq.TrackingspoaFacade;
import org.entidades.sfccbdmq.Proyectospoa;
import org.entidades.sfccbdmq.Trackingspoa;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.Auxiliar;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesPoaBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.ProyectosFacade;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Proyectos;
import org.errores.sfccbdmq.BorrarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.component.fileentry.FileEntry;
import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.icefaces.ace.component.fileentry.FileEntryResults;
import org.icefaces.ace.event.TextChangeEvent;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author luis
 */
@ManagedBean(name = "proyectosDireccionPoa")
@ViewScoped
public class ProyectosDireccionPoaBean {

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{imagenesPoa}")
    private ImagenesPoaBean imagenesPoaBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;

    private Formulario formulario = new Formulario();
    private Formulario formularioRenombrar = new Formulario();
    private LazyDataModel<Proyectospoa> proyectos;
    private Proyectospoa proyecto;
    private Proyectospoa programa;
    private Proyectospoa proyectoPadre;
    private Proyectospoa proyectoSeleccionado;
    private String codigo;
    private String codigoAnterior;
    private String nombre;
    private String titulo;
    private Integer tipoBuscar = 2;
    private Integer anio;
    private int anioActual;
    private int longitud;
    private int nivel = 1;
    private List<Proyectospoa> listaProyecto;
    private List sumatorio;
    private String ingreso;
    private String imputable;
    private String nombreAnterior;
    private String observacionReforma;
    private String direccion;

    private Date vigente;
    private Date desde;
    private Date hasta;

    private String separador = ";";
    private Integer nivelProyecto = 0;

    //Financiero
    private String tipo = "PROG";
    private Proyectos pf;

    @EJB
    private ProyectospoaFacade ejbProyectospoa;
    @EJB
    private AsignacionespoaFacade ejbAsignaciones;
    @EJB
    private ReformaspoaFacade ejbReformas;
    @EJB
    private DetallecertificacionespoaFacade ejbCertificaciones;
    @EJB
    private TrackingspoaFacade ejbTrackingspoa;

    //Financiero
    @EJB
    private ProyectosFacade ejbProyectosF;

    public ProyectosDireccionPoaBean() {
        proyectos = new LazyDataModel<Proyectospoa>() {

            @Override
            public List<Proyectospoa> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
        sumatorio = new LinkedList();
        sumatorio.add("Partidas");
        sumatorio.add("Reformas");
        sumatorio.add("Certificaciones");
    }

    public String crear(Proyectospoa proyectoPadre) {
        if (anio == null || anio <= 0) {
            MensajesErrores.advertencia("Año es obligatorio para crear proyecto");
            return null;
        }
        if (anio < anioActual) {
            MensajesErrores.advertencia("No se pueden crear proyectos para años anteriores");
            return null;
        }
        this.proyectoPadre = proyectoPadre;

        proyecto = new Proyectospoa();
        proyecto.setActivo(true);
        proyecto.setAlineado(false);
        proyecto.setDefinitivo(false);
        proyecto.setPadre(proyectoPadre);
        proyecto.setNivel(nivel);
        proyecto.setAnio(anio);
        proyecto.setImputable(Boolean.FALSE);

        //financiero
        pf = new Proyectos();
        pf.setTipo(tipo);
        pf.setAnio(anio);
        pf.setImputable(Boolean.FALSE);

        Codigos c = codigosBean.traerCodigo("LONPRO", String.valueOf(anio));
        if (c == null) {
            MensajesErrores.fatal("Falla en configuración, asegurese que el formato de los proyectos esta creada en códigos");
            return null;
        }
        if ((c.getParametros() == null) || (c.getParametros().isEmpty())) {
            MensajesErrores.fatal("Falla en configuración, asegurese que el formato  de los proyectos esta creada en códigos");
            return null;
        }
        //Aquí ver el nivel
        String formato = c.getParametros().replace(".", "#");
        String[] sinpuntos = formato.split("#");

        //Títulos
        String formatoTitulos = c.getDescripcion().replace(".", "#");
        String[] sinpuntosTitulos = formatoTitulos.split("#");

        int laux = 0;
        longitud = 0;
        if (proyectoPadre == null) {
            longitud = sinpuntos[0].length();
            titulo = sinpuntosTitulos[0];
        } else {
            codigoAnterior = (proyectoPadre.getCodigo());
            proyecto.setIngreso(proyectoPadre.getIngreso());
            proyecto.setImputable(Boolean.FALSE);

            //Financiero
            pf.setIngreso(proyectoPadre.getIngreso());
            pf.setImputable(Boolean.FALSE);

            for (int i = 0; i < sinpuntos.length; i++) {
                String sinBlancos = sinpuntos[i].trim();
                laux += sinBlancos.length();
                if (laux == codigoAnterior.length()) {
                    longitud = sinpuntos[i + 1].length();
                    if (i + 2 == sinpuntos.length) {
                        proyecto.setImputable(Boolean.TRUE);
                        pf.setImputable(Boolean.TRUE);//Financiero
                    } else {
                        proyecto.setImputable(Boolean.FALSE);
                        pf.setImputable(Boolean.FALSE);//Financiero
                    }
                    i = 1000;
                }
            }

            int p = 0;
            int m = 0;
            for (int i = 0; i < sinpuntos.length - 1; i++) {
                m += sinpuntos[i].length();
                if (m <= codigoAnterior.length()) {
                    p++;
                }

            }
            titulo = sinpuntosTitulos[p];

        }
//        longitud=Integer.parseInt(c.getParametros());
        imagenesPoaBean.setListaDocumentos(new LinkedList<>());
        formulario.insertar();

        return null;
    }

    public String modificar(Proyectospoa proyecto) {
        this.proyecto = proyecto;
        if (proyecto.getPadre() != null) {
            proyecto.setIngreso(proyecto.getPadre().getIngreso());
        }
        imagenesPoaBean.setProyecto(proyecto);
        imagenesPoaBean.buscarDocumentos();
        formulario.editar();

        Codigos c = codigosBean.traerCodigo("LONPRO", String.valueOf(anio));
        if (c == null) {
            MensajesErrores.fatal("Falla en configuración, asegurese que el formato de los proyectos esta creada en códigos");
            return null;
        }
        if ((c.getParametros() == null) || (c.getParametros().isEmpty())) {
            MensajesErrores.fatal("Falla en configuración, asegurese que el formato  de los proyectos esta creada en códigos");
            return null;
        }
        //Aquí ver el nivel
        String formato = c.getParametros().replace(".", "#");
        String[] sinpuntos = formato.split("#");

        //Títulos
        String formatoTitulos = c.getDescripcion().replace(".", "#");
        String[] sinpuntosTitulos = formatoTitulos.split("#");

        proyectoPadre = proyecto.getPadre();
        if (proyectoPadre == null) {
            longitud = sinpuntos[0].length();
            titulo = sinpuntosTitulos[0];
        } else {
            codigoAnterior = (proyectoPadre.getCodigo());
            int p = 0;
            int m = 0;
            for (int i = 0; i < sinpuntos.length - 1; i++) {
                m += sinpuntos[i].length();
                if (m <= codigoAnterior.length()) {
                    p++;
                }

            }
            titulo = sinpuntosTitulos[p];
        }
        return null;
    }

    public String modificarNombre(Proyectospoa proyecto) {
        this.proyecto = proyecto;
        if (proyecto.getPadre() != null) {
            proyecto.setIngreso(proyecto.getPadre().getIngreso());
        }
//        imagenesPoaBean.setProyecto(proyecto);
//        imagenesPoaBean.buscarDocumentos();
        formulario.editar();

        Codigos c = codigosBean.traerCodigo("LONPRO", String.valueOf(anio));
        if (c == null) {
            MensajesErrores.fatal("Falla en configuración, asegurese que el formato de los proyectos esta creada en códigos");
            return null;
        }
        if ((c.getParametros() == null) || (c.getParametros().isEmpty())) {
            MensajesErrores.fatal("Falla en configuración, asegurese que el formato  de los proyectos esta creada en códigos");
            return null;
        }
        //Aquí ver el nivel
        String formato = c.getParametros().replace(".", "#");
        String[] sinpuntos = formato.split("#");

        //Títulos
        String formatoTitulos = c.getDescripcion().replace(".", "#");
        String[] sinpuntosTitulos = formatoTitulos.split("#");

        proyectoPadre = proyecto.getPadre();
        if (proyectoPadre == null) {
            longitud = sinpuntos[0].length();
            titulo = sinpuntosTitulos[0];
        } else {
            codigoAnterior = (proyectoPadre.getCodigo());
            int p = 0;
            int m = 0;
            for (int i = 0; i < sinpuntos.length - 1; i++) {
                m += sinpuntos[i].length();
                if (m <= codigoAnterior.length()) {
                    p++;
                }

            }
            titulo = sinpuntosTitulos[p];
        }
        return null;
    }

    public String eliminar(Proyectospoa proyecto) {
        this.proyecto = proyecto;
        getImagenesPoaBean().setProyecto(proyecto);
        getImagenesPoaBean().buscarDocumentos();
        formulario.eliminar();

        Codigos c = codigosBean.traerCodigo("LONPRO", String.valueOf(anio));
        if (c == null) {
            MensajesErrores.fatal("Falla en configuración, asegurese que el formato de los proyectos esta creada en códigos");
            return null;
        }
        if ((c.getParametros() == null) || (c.getParametros().isEmpty())) {
            MensajesErrores.fatal("Falla en configuración, asegurese que el formato  de los proyectos esta creada en códigos");
            return null;
        }
        //Aquí ver el nivel
        String formato = c.getParametros().replace(".", "#");

        String[] sinpuntos = formato.split("#");

        //Títulos
        String formatoTitulos = c.getDescripcion().replace(".", "#");
        String[] sinpuntosTitulos = formatoTitulos.split("#");

        proyectoPadre = proyecto.getPadre();
        if (proyectoPadre == null) {
            longitud = sinpuntos[0].length();
            titulo = sinpuntosTitulos[0];
        } else {
            codigoAnterior = (proyectoPadre.getCodigo());
            int p = 0;
            int m = 0;
            for (int i = 0; i < sinpuntos.length - 1; i++) {
                m += sinpuntos[i].length();
                if (m <= codigoAnterior.length()) {
                    p++;
                }

            }
            titulo = sinpuntosTitulos[p];
        }

        return null;
    }

    public String insertar() {
        Calendar c = Calendar.getInstance();
        anio = c.get(Calendar.YEAR);
        if ((proyecto.getCodigo() == null) || (proyecto.getCodigo().isEmpty())) {
            MensajesErrores.advertencia("Es necesario un código de proyecto");
            return null;
        }
        if ((proyecto.getNombre() == null) || (proyecto.getNombre().isEmpty())) {
            MensajesErrores.advertencia("Es necesario un nombre de proyecto");
            return null;
        }
        if (proyecto.getAnio() < anio) {
            MensajesErrores.advertencia("Año no puede ser menor al actual");
            return null;
        }
        if (proyecto.getCodigo().length() != longitud) {
            MensajesErrores.advertencia("Código debe tener " + longitud + "  carcateres");
            return null;
        }
        if (proyecto.getImputable() && proyecto.getDireccion() == null) {
            MensajesErrores.advertencia("Dirección es necesaria");
            return null;
        }
        // ver si existe
        String codigoBuscar = proyecto.getCodigo();
        if (proyectoPadre != null) {
            codigoBuscar = proyectoPadre.getCodigo() + proyecto.getCodigo();
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.codigo=:codigo and o.anio=:anio and o.activo=true");
        parametros.put("codigo", codigoBuscar);
        parametros.put("anio", proyecto.getAnio());
        try {
            int total = ejbProyectospoa.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Código ya existe para este año");
                return null;
            }

            //Financiero
            parametros = new HashMap();
            parametros.put(";where", "o.codigo=:codigo and o.anio=:anio");
            parametros.put("codigo", codigoBuscar);
            parametros.put("anio", pf.getAnio());
            int totalf = ejbProyectosF.contar(parametros);
            List<Proyectos> lista = ejbProyectosF.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                Proyectos p = lista.get(0);
                p.setNombre(proyecto.getNombre());
                ejbProyectosF.edit(p, seguridadbean.getLogueado().getUserid());
            }
            //Fin Financiero
            proyecto.setCodigo(codigoBuscar.toUpperCase());
            ejbProyectospoa.create(proyecto, seguridadbean.getLogueado().getUserid());
            //financiero
            if (totalf == 0) {
                pf.setCodigo(codigoBuscar.toUpperCase());
                pf.setNombre(proyecto.getNombre());
                pf.setObservaciones(proyecto.getObservaciones());
                pf.setIngreso(proyecto.getIngreso());
                ejbProyectosF.create(pf, seguridadbean.getLogueado().getUserid());
            }
            //Fin Financiero
            if (proyecto.getImputable()) {
                Trackingspoa tracking = new Trackingspoa();
                tracking.setReformanombre(false);
                tracking.setFecha(new Date());
                tracking.setProyecto(proyecto);
                tracking.setResponsable(seguridadbean.getLogueado().getApellidos() + " " + seguridadbean.getLogueado().getNombres());
                tracking.setObservaciones("Producto imputable creado");
                ejbTrackingspoa.create(tracking, seguridadbean.getLogueado().getUserid());
            }

            imagenesPoaBean.setProyecto(proyecto);
            imagenesPoaBean.insertarDocumentos("proyectos");
        } catch (ConsultarException | InsertarException | IOException | GrabarException  ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ProyectosDireccionPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public String grabar() {

        if ((proyecto.getNombre() == null) || (proyecto.getNombre().isEmpty())) {
            MensajesErrores.advertencia("Nombre es obligatorio");
            return null;
        }

        if (proyecto.getImputable() && proyecto.getDireccion() == null) {
            MensajesErrores.advertencia("Dirección es necesaria");
            return null;
        }
        try {

            ejbProyectospoa.edit(proyecto, seguridadbean.getLogueado().getUserid());

            //financiero
            String codigoBuscar = proyecto.getCodigo();
            Map parametros = new HashMap();
            parametros.put(";where", "o.codigo=:codigo and o.anio=:anio");
            parametros.put("codigo", codigoBuscar);
            parametros.put("anio", pf.getAnio());
            if (codigoBuscar.equals(proyecto.getCodigo())) {
                try {
                    pf.setNombre(proyecto.getNombre());
                    pf.setObservaciones(proyecto.getObservaciones());
                    pf.setIngreso(proyecto.getIngreso());
                    ejbProyectosF.edit(pf, seguridadbean.getLogueado().getUserid());
                } catch (org.errores.sfccbdmq.GrabarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
                    Logger.getLogger(ProyectosPoaBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            //Fin financiero

            if (proyecto.getImputable()) {
                Trackingspoa tracking = new Trackingspoa();
                tracking.setReformanombre(false);
                tracking.setFecha(new Date());
                tracking.setProyecto(proyecto);
                tracking.setResponsable(seguridadbean.getLogueado().getApellidos() + " " + seguridadbean.getLogueado().getNombres());
                tracking.setObservaciones("Producto imputable modificado");
                ejbTrackingspoa.create(tracking, seguridadbean.getLogueado().getUserid());
            }

            getImagenesPoaBean().setProyecto(proyecto);
            getImagenesPoaBean().insertarDocumentos("proyectos");

        } catch (GrabarException | InsertarException | IOException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(ProyectosDireccionPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String grabarSoloPoa() {

        if ((proyecto.getNombre() == null) || (proyecto.getNombre().isEmpty())) {
            MensajesErrores.advertencia("Nombre es obligatorio");
            return null;
        }

        if (proyecto.getImputable() && proyecto.getDireccion() == null) {
            MensajesErrores.advertencia("Dirección es necesaria");
            return null;
        }
        try {

            ejbProyectospoa.edit(proyecto, seguridadbean.getLogueado().getUserid());

            //financiero
            if (proyecto.getImputable()) {
                Trackingspoa tracking = new Trackingspoa();
                tracking.setReformanombre(false);
                tracking.setFecha(new Date());
                tracking.setProyecto(proyecto);
                tracking.setResponsable(seguridadbean.getLogueado().getApellidos() + " " + seguridadbean.getLogueado().getNombres());
                tracking.setObservaciones("Producto modificado en reforma");
                ejbTrackingspoa.create(tracking, seguridadbean.getLogueado().getUserid());
            }

        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(ProyectosDireccionPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borrar() {
        try {
            // poco más dificil hay que ver que no exist movimientos
            Map parametros = new HashMap();
            parametros.put(";where", "o.codigo like :codigo and o.anio=:anio and o.activo=true");
            parametros.put("codigo", proyecto.getCodigo() + "%");
            parametros.put("anio", proyecto.getAnio());
            int total = ejbProyectospoa.contar(parametros);
            if (total > 1) {
                MensajesErrores.advertencia("Existen proyectos de este año con códigos inferiores borre estos primeros");
                return null;
            }
            parametros = new HashMap();
            parametros.put(";where", "o.proyecto =:proyecto");
            parametros.put("proyecto", proyecto);
            total = ejbAsignaciones.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Existen asignaciones  de este año inferiores borre estas primeros");
                return null;
            }
//            if (ejbTrackingspoa.contar(parametros) > 0) {
//                proyecto.setActivo(false);
//                ejbProyectospoa.edit(proyecto, seguridadbean.getLogueado().getUserid());
//            } else {
//                ejbProyectospoa.remove(proyecto, seguridadbean.getLogueado().getUserid());
//            }
            proyecto.setActivo(false);
            ejbProyectospoa.edit(proyecto, seguridadbean.getLogueado().getUserid());

            //financiero
            String codigoBuscar = proyecto.getCodigo();
            parametros = new HashMap();
            parametros.put(";where", "o.codigo=:codigo and o.anio=:anio");
            parametros.put("codigo", codigoBuscar);
            parametros.put("anio", pf.getAnio());
            try {
                if (codigoBuscar.equals(proyecto.getCodigo())) {
                    ejbProyectosF.remove(pf, seguridadbean.getLogueado().getUserid());
                }
            } catch (BorrarException ex) {
                MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
                Logger.getLogger(ProyectosPoaBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            //Fin financiero

        } catch (ConsultarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String buscar() {
        proyectos = new LazyDataModel<Proyectospoa>() {

            @Override
            public List<Proyectospoa> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                if (scs.length == 0) {
                    parametros.put(";orden", "o.codigo");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }
                String where = "  o.anio=:anio and o.activo=true and length(o.codigo)>:longitud";
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();
                    if (valor.toUpperCase().equals("SI") || valor.toUpperCase().equals("NO")) {
                        where += " and o." + clave + " =" + (valor.toUpperCase().equals("SI") ? "true" : "false");
                    } else if (valor.toUpperCase().equals("")) {
                        where += " and o." + clave + " is not null";
                    } else {
                        where += " and upper(o." + clave + ") like :" + clave;
                        parametros.put(clave, valor.toUpperCase() + "%");
                    }
                }
                parametros.put("anio", getAnio());
                parametros.put("longitud", nivelProyecto);

                if (!((codigo == null) || (codigo.isEmpty()))) {
                    where += " and o.codigo like :codigo";
                    parametros.put("codigo", getCodigo() + "%");
                }
                if (!(nombre == null || nombre.isEmpty())) {
                    where += " and upper(o.nombre) like :nombre";
                    parametros.put("nombre", getNombre().toUpperCase() + "%");
                }
                if (!(direccion == null || direccion.isEmpty())) {
                    where += " and upper(o.direccion) =:direccion";
                    parametros.put("direccion", getDireccion().toUpperCase());
                }
                int total = 0;
                try {
                    parametros.put(";where", where);
                    total = ejbProyectospoa.contar(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                int endIndex = i + pageSize;
                if (endIndex > total) {
                    endIndex = total;
                }
                parametros.put(";inicial", i);
                parametros.put(";final", endIndex);
                getProyectospoa().setRowCount(total);
                try {
                    return ejbProyectospoa.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        return null;
    }

    public Proyectospoa traer(Integer id) throws ConsultarException {
        return ejbProyectospoa.find(id);
    }

    public Proyectospoa traerCodigo(String codigoProyecto) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.codigo = :proyecto");
        parametros.put("proyecto", codigoProyecto);
        try {
            List<Proyectospoa> listaProyectospoa = ejbProyectospoa.encontarParametros(parametros);
            for (Proyectospoa p : listaProyectospoa) {
                return p;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ProyectosDireccionPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Proyectospoa traerProyectoAsignacion(String codigoProyecto) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.codigo= like proyecto");
        parametros.put("proyecto", codigoProyecto);
        try {
            List<Proyectospoa> listaProyectospoa = ejbProyectospoa.encontarParametros(parametros);
            for (Proyectospoa p : listaProyectospoa) {
                return p;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ProyectosDireccionPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public boolean getMostrarAnio() {
        if (formulario.isNuevo()) {
            return proyectoPadre == null;
        }
        return false;
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
     * @return the sumatorio
     */
    public List getSumatorio() {
        return sumatorio;
    }

    /**
     * @param sumatorio the sumatorio to set
     */
    public void setSumatorio(List sumatorio) {
        this.sumatorio = sumatorio;
    }

    @PostConstruct
    private void activar() {
        vigente = getConfiguracionBean().getConfiguracion().getPinicialpresupuesto();
        desde = getConfiguracionBean().getConfiguracion().getPinicialpresupuesto();
        hasta = getConfiguracionBean().getConfiguracion().getPfinalpresupuesto();
        Calendar ca = Calendar.getInstance();
        ca.setTime(desde);
        anio = ca.get(Calendar.YEAR);

        if (seguridadbean.traerDireccionEmpleado() != null) {
            if (seguridadbean.traerDireccionEmpleado().getCodigo() != null) {
                direccion = seguridadbean.traerDireccionEmpleado().getCodigo();
            } else {
                direccion = null;
            }
        }
        Codigos c = codigosBean.traerCodigo("LONPRO", String.valueOf(anio));
        if (c != null && c.getParametros() != null && !c.getParametros().isEmpty()) {
            int total = c.getParametros().replaceAll("\\.", "").length();
            String formato = c.getParametros().replace(".", "#");
            String[] sinpuntos = formato.split("#");
            int grupos = sinpuntos.length;
            if (grupos > 2) {
                int total1 = sinpuntos[grupos - 1].length() + sinpuntos[grupos - 2].length();
                nivelProyecto = total - total1;
            }
        }
    }

    public void codigoChangeEventHandler(TextChangeEvent event) {
        proyectoSeleccionado = null;
        String newWord = event.getNewValue() != null ? (String) event.getNewValue() : "";
        if ((newWord == null) || (newWord.isEmpty())) {
            proyectoSeleccionado = null;
            return;
        }
        // traer la consulta
        Map parametros = new HashMap();
        String where = "  o.imputable=true and o.activo=true";

        if (direccion != null) {
            where += " and upper(o.direccion)=:direccion";
            parametros.put("direccion", direccion);
        }
        if (tipoBuscar == 1) {//codigo
            where += " and  upper(o.codigo) like :codigo";
            parametros.put("codigo", newWord.toUpperCase() + "%");
            parametros.put(";orden", " o.codigo");
        } else if (tipoBuscar == 2) {//nombre
            where += " and  upper(o.nombre) like :codigo";
            parametros.put("codigo", "%" + newWord.toUpperCase() + "%");
            parametros.put(";orden", " o.nombre");
        }
        parametros.put(";where", where);

        listaProyecto = null;
        // Contadores
        parametros.put(";inicial", 0);
        parametros.put(";final", 10);
        try {
            listaProyecto = ejbProyectospoa.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }
    }

    public void cambiaCodigo(ValueChangeEvent event) {
        proyectoSeleccionado = null;
        if (listaProyecto == null) {
            return;
        }
        String newWord = (String) event.getNewValue();
        for (Proyectospoa p : listaProyecto) {
            String aComparar = tipoBuscar == 1 ? p.getCodigo() : p.getNombre();
            if (aComparar.replaceAll("\\s", "").compareToIgnoreCase(newWord.replaceAll("\\s", "")) == 0) {
                proyectoSeleccionado = p;
                codigo = p.getCodigo();
            }
        }
    }

    public void codigoGastoChangeEventHandler(TextChangeEvent event) {
        proyectoSeleccionado = null;
        String newWord = event.getNewValue() != null ? (String) event.getNewValue() : "";
        if ((newWord == null) || (newWord.isEmpty())) {
            proyectoSeleccionado = null;
            return;
        }
        // traer la consulta
        Map parametros = new HashMap();
        String where = "  o.imputable=true and o.activo=true and o.ingreso=false";

        if (direccion != null) {
            where += " and upper(o.direccion)=:direccion";
            parametros.put("direccion", direccion);
        }
        if (tipoBuscar == 1) {//codigo
            where += " and  upper(o.codigo) like :codigo";
            parametros.put("codigo", newWord.toUpperCase() + "%");
            parametros.put(";orden", " o.codigo");
        } else if (tipoBuscar == 2) {//nombre
            where += " and  upper(o.nombre) like :codigo";
            parametros.put("codigo", "%" + newWord.toUpperCase() + "%");
            parametros.put(";orden", " o.nombre");
        }
        parametros.put(";where", where);

        listaProyecto = null;
        // Contadores
        parametros.put(";inicial", 0);
        parametros.put(";final", 10);
        try {
            listaProyecto = ejbProyectospoa.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }
    }

    public List<Auxiliar> getTitulos() {
        List<Auxiliar> lista = new LinkedList<>();
        Codigos c = codigosBean.traerCodigo("LONPRO", String.valueOf(anio));
        if (c == null) {
            MensajesErrores.fatal("Falla en configuración, asegurese que la longitud de los proyectos esta creada en códigos");
            return null;
        }
        if ((c.getParametros() == null) || (c.getParametros().isEmpty())) {
            MensajesErrores.fatal("Falla en configuración, asegurese que la longitud de los proyectos esta creada en códigos");
            return null;
        }
        // aquí ver el nivel
        String formato = c.getParametros().replace(".", "#");
        String formatoTitulos = c.getDescripcion().replace(".", "#");
        String[] sinpuntos = formato.split("#");
        String[] sinpuntosTitulos = formatoTitulos.split("#");
        int longitudAux = 0;
        for (int i = 0; i < sinpuntos.length - 1; i++) {
            Auxiliar a = new Auxiliar();
            String sinBlancos = sinpuntos[i].trim();
            longitudAux += sinBlancos.length();
            a.setTitulo1(sinpuntosTitulos[i]);
            a.setIndice(longitudAux);
            lista.add(a);
        }
        return lista;
    }

    public String dividir(Auxiliar a, Proyectospoa p) {
        if (p == null) {
            return null;
        }
        if (a == null) {
            return null;
        }
        String codigoTemporal = p.getCodigo().substring(0, a.getIndice());
        Map parametros = new HashMap();
        parametros.put(";where", "o.codigo=:codigo");
        parametros.put("codigo", codigoTemporal);
        try {
            List<Proyectospoa> lpro = ejbProyectospoa.encontarParametros(parametros);
            for (Proyectospoa p1 : lpro) {
                return p1.getCodigo() + " - " + p1.getNombre();
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ProyectosDireccionPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String dividir1(Auxiliar a, Proyectospoa p) {
        if (p == null) {
            return null;
        }
        if (a == null) {
            return null;
        }
        String codigoTemporal = p.getCodigo().substring(0, a.getIndice());
        Map parametros = new HashMap();
        parametros.put(";where", "o.codigo=:codigo");
        parametros.put("codigo", codigoTemporal);
        try {
            List<Proyectospoa> lpro = ejbProyectospoa.encontarParametros(parametros);
            for (Proyectospoa p1 : lpro) {
                return p1.getNombre();
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ProyectosDireccionPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String getBlancos() {
        return "<br><br> <br> <br> <br>";
    }

    public SelectItem[] traerCombosino() {
        List<String> lista = new LinkedList<>();
        lista.add("");
        lista.add("Si");
        lista.add("No");
        return Combos.getSelectItems(lista, false);
    }

    public String renombrar(Proyectospoa pro) {
        proyecto = pro;
        nombreAnterior = proyecto.getNombre();
        formularioRenombrar.editar();
        observacionReforma = null;

        Codigos c = codigosBean.traerCodigo("LONPRO", String.valueOf(anio));
        if (c == null) {
            MensajesErrores.fatal("Falla en configuración, asegurese que el formato de los proyectos esta creada en códigos");
            return null;
        }
        if ((c.getParametros() == null) || (c.getParametros().isEmpty())) {
            MensajesErrores.fatal("Falla en configuración, asegurese que el formato  de los proyectos esta creada en códigos");
            return null;
        }
        //Aquí ver el nivel
        String formato = c.getParametros().replace(".", "#");
        String[] sinpuntos = formato.split("#");

        //Títulos
        String formatoTitulos = c.getDescripcion().replace(".", "#");
        String[] sinpuntosTitulos = formatoTitulos.split("#");

        proyectoPadre = proyecto.getPadre();
        if (proyectoPadre == null) {
            longitud = sinpuntos[0].length();
            titulo = sinpuntosTitulos[0];
        } else {
            codigoAnterior = (proyectoPadre.getCodigo());
            int p = 0;
            int m = 0;
            for (int i = 0; i < sinpuntos.length - 1; i++) {
                m += sinpuntos[i].length();
                if (m <= codigoAnterior.length()) {
                    p++;
                }

            }
            titulo = sinpuntosTitulos[p];
        }
        return null;
    }

    public String grabarRenombrar() {
        if ((proyecto.getNombre() == null) || (proyecto.getNombre().trim().isEmpty())) {
            MensajesErrores.advertencia("Nombre es necesario");
            return null;
        }

        if (observacionReforma == null || observacionReforma.trim().isEmpty()) {
            MensajesErrores.advertencia("Observaciones son necesarias");
            return null;
        }

        try {

            ejbProyectospoa.edit(proyecto, seguridadbean.getLogueado().getUserid());

            //Financiero
            String codigoBuscar = proyecto.getCodigo();
            Map parametros = new HashMap();
            parametros.put(";where", "o.codigo=:codigo and o.anio=:anio");
            parametros.put("codigo", codigoBuscar);
            parametros.put("anio", anio);
            try {
                List<Proyectos> lista = ejbProyectosF.encontarParametros(parametros);
                for (Proyectos p : lista) {
                    if (codigoBuscar.equals(p.getCodigo())) {
                        try {
                            p.setNombre(proyecto.getNombre());
                            p.setObservaciones(proyecto.getObservaciones());
                            ejbProyectosF.edit(p, seguridadbean.getLogueado().getUserid());
                        } catch (org.errores.sfccbdmq.GrabarException ex) {
                            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
                            Logger.getLogger(ProyectosPoaBean.class.getName()).log(Level.SEVERE, null, ex);

                        }
                    }
                }
            } catch (org.errores.sfccbdmq.ConsultarException ex) {
                Logger.getLogger(ProyectosPoaBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            //Fin Financiero

            Trackingspoa tracking = new Trackingspoa();
            tracking.setReformanombre(true);
            tracking.setFecha(new Date());
            tracking.setProyecto(proyecto);
            tracking.setResponsable(seguridadbean.getLogueado().getApellidos() + " " + seguridadbean.getLogueado().getNombres());
            tracking.setObservaciones("Nombre de " + titulo + " reformado de \"" + nombreAnterior.toUpperCase() + "\" a \"" + proyecto.getNombre().toUpperCase() + "\"");
            tracking.setObservaciones(tracking.getObservaciones() + "\nObservaciones:" + observacionReforma);
            ejbTrackingspoa.create(tracking, seguridadbean.getLogueado().getUserid());

        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(ProyectosDireccionPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioRenombrar.cancelar();
        return null;
    }

    public void archivoListener(FileEntryEvent e) {
        FileEntry fe = (FileEntry) e.getComponent();
        FileEntryResults results = fe.getResults();
        File parent = null;
        Calendar cAnio = Calendar.getInstance();

        //get data About File
        for (FileEntryResults.FileInfo i : results.getFiles()) {

            if (i.isSaved()) {

                File file = i.getFile();
                if (file != null) {
                    try {
                        parent = file.getParentFile();

                        BufferedReader entrada = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));

                        String sb = entrada.readLine();
                        String[] cabecera = sb.split(separador);// lee los caracteres en el arreglo
                        int registro = 0;
                        while ((sb = entrada.readLine()) != null) {
                            registro++;
                            Proyectospoa asig = new Proyectospoa();
                            String[] aux = sb.split(separador);// lee los caracteres en el arreglo
                            for (int j = 0; j < aux.length; j++) {
                                if (j < cabecera.length) {
                                    ubicar(asig, cabecera[j].toLowerCase(), aux[j].toUpperCase());
                                }
                            }
                            asig.setActivo(Boolean.TRUE);
                            asig.setAnio(anio);
                            if (asig.getCodigo() == null || asig.getCodigo().trim().isEmpty()) {
                                MensajesErrores.advertencia("Código en línea " + registro + " no es válido");
                            } else if (asig.getNombre() == null || asig.getNombre().trim().isEmpty()) {
                                MensajesErrores.advertencia("Nombre en línea " + registro + " no es válido");
                            } else {
                                Proyectospoa existe = traerCodigo(asig.getCodigo());
                                if (existe == null) {
                                    ejbProyectospoa.create(asig, seguridadbean.getLogueado().getUserid());
                                } else {
                                    existe.setActivo(Boolean.TRUE);
                                    ejbProyectospoa.edit(existe, seguridadbean.getLogueado().getUserid());
                                }
                            }
                        }
                        entrada.close();
                    } catch (UnsupportedEncodingException ex) {
                        MensajesErrores.advertencia(ex.getMessage() + " - " + ex.getCause());
                        Logger.getLogger(AsignacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (FileNotFoundException | InsertarException | GrabarException ex) {
                        MensajesErrores.advertencia(ex.getMessage() + " - " + ex.getCause());
                        Logger.getLogger(AsignacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        MensajesErrores.advertencia(ex.getMessage() + " - " + ex.getCause());
                        Logger.getLogger(AsignacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    MensajesErrores.fatal("Archivo no puede ser cargado: "
                            + i.getStatus().getFacesMessage(FacesContext.getCurrentInstance(), fe, i).getSummary());
                }
            }

        }
    }

    private void ubicar(Proyectospoa proyecto, String titulo, String valor) {
        if (titulo.contains("codigo")) {
            proyecto.setCodigo(valor);
        } else if (titulo.contains("nombre")) {
            proyecto.setNombre(valor);
        } else if (titulo.contains("nivel")) {
            try {
                proyecto.setNivel(Integer.parseInt(valor));
            } catch (Exception e) {
                return;
            }
        } else if (titulo.contains("ingreso")) {
            proyecto.setIngreso(valor.toUpperCase().equals("true"));
        } else if (titulo.contains("imputable")) {
            proyecto.setImputable(valor.toUpperCase().equals("true"));
        } else if (titulo.contains("direccion") || titulo.contains("dirección")) {
            proyecto.setDireccion(valor);
        }
    }

    public BigDecimal getTotalIngresos() {
        if (anio == 0) {
            return new BigDecimal(0);
        }
        String fila = (String) sumatorio.get(formulario.getFila().getRowIndex());
        // sumar de todo el anio
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPfinalpresupuesto());
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        parametros.put("anio", anio);
        BigDecimal retorno = new BigDecimal(0);
        try {
            if (fila.equalsIgnoreCase("Partidas")) {
                parametros.put(";where", "o.partida.ingreso=true and o.proyecto.anio=:anio ");
                retorno = ejbAsignaciones.sumarCampo(parametros);
            } else if (fila.equalsIgnoreCase("Reformas")) {
                parametros.put(";where", "o.asignacion.partida.ingreso=true and o.asignacion.proyecto.anio=:anio ");
                retorno = ejbReformas.sumarCampo(parametros);
            } else if (fila.equalsIgnoreCase("Certificaciones")) {
                parametros.put(";where", "o.asignacion.partida.ingreso=true "
                        + "and o.asignacion.proyecto.anio=:anio and o.certificacion.impreso=true and o.certificacion.anulado=false");
                retorno = ejbCertificaciones.sumarCampo(parametros);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ProyectosDireccionPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    public BigDecimal getTotalEgresos() {
        if (anio == 0) {
            return new BigDecimal(0);
        }
        // sumar de todo el anio
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        parametros.put("anio", anio);
        BigDecimal retorno = new BigDecimal(0);
        String fila = (String) sumatorio.get(formulario.getFila().getRowIndex());
        try {
            if (fila.equalsIgnoreCase("Partidas")) {
                parametros.put(";where", "o.partida.ingreso=false and o.proyecto.anio=:anio ");
                retorno = ejbAsignaciones.sumarCampo(parametros);
            } else if (fila.equalsIgnoreCase("Reformas")) {
                parametros.put(";where", "o.asignacion.partida.ingreso=false and o.asignacion.proyecto.anio=:anio ");
                retorno = ejbReformas.sumarCampo(parametros);
            } else if (fila.equalsIgnoreCase("Certificaciones")) {
                parametros.put(";where", "o.asignacion.partida.ingreso=false and o.asignacion.proyecto.anio=:anio and o.certificacion.impreso=true and o.certificacion.anulado=false");
                retorno = ejbCertificaciones.sumarCampo(parametros);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ProyectosDireccionPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    public BigDecimal getTotalIngresosFuente(Codigos c, String fila) {
        if (anio == 0) {
            return new BigDecimal(0);
        }
        Map parametros = new HashMap();

        parametros.put(";campo", "o.valor");
        parametros.put("fuente", c.getCodigo());
        parametros.put("anio", anio);
        BigDecimal retorno = new BigDecimal(0);
        try {
            if (fila.equalsIgnoreCase("Partidas")) {
                parametros.put(";where", "o.partida.ingreso=true and o.proyecto.anio=:anio and o.fuente=:fuente");
                retorno = ejbAsignaciones.sumarCampo(parametros);
            } else if (fila.equalsIgnoreCase("Reformas")) {
                parametros.put(";where", "o.asignacion.partida.ingreso=true and o.asignacion.proyecto.anio=:anio and o.asignacion.fuente=:fuente");
                retorno = ejbReformas.sumarCampo(parametros);
            } else if (fila.equalsIgnoreCase("Certificaciones")) {
                parametros.put(";where", "o.asignacion.partida.ingreso=true and o.asignacion.proyecto.anio=:anio and o.asignacion.fuente=:fuente and o.certificacion.impreso=true and o.certificacion.anulado=false");
                retorno = ejbCertificaciones.sumarCampo(parametros);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ProyectosDireccionPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    public BigDecimal getTotalEgresosFuente(Codigos c, String fila) {
        if (anio == 0) {
            return new BigDecimal(0);
        }
        // sumar de todo el anio
        Map parametros = new HashMap();

        parametros.put(";campo", "o.valor");
        parametros.put("fuente", c.getCodigo());
        parametros.put("anio", anio);
        BigDecimal retorno = new BigDecimal(0);
        try {
            if (fila.equalsIgnoreCase("Partidas")) {
                parametros.put(";where", "o.partida.ingreso=false and o.proyecto.anio=:anio and o.fuente=:fuente");
                retorno = ejbAsignaciones.sumarCampo(parametros);
            } else if (fila.equalsIgnoreCase("Reformas")) {
                parametros.put(";where", "o.asignacion.partida.ingreso=false and o.asignacion.proyecto.anio=:anio and o.asignacion.fuente=:fuente");
                retorno = ejbReformas.sumarCampo(parametros);
            } else if (fila.equalsIgnoreCase("Certificaciones")) {
                parametros.put(";where", "o.asignacion.partida.ingreso=false and o.asignacion.proyecto.anio=:anio and o.asignacion.fuente=:fuente and o.certificacion.impreso=true and o.certificacion.anulado=false");
                retorno = ejbCertificaciones.sumarCampo(parametros);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ProyectosDireccionPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    /**
     * @return the imagenesPoaBean
     */
    public ImagenesPoaBean getImagenesPoaBean() {
        return imagenesPoaBean;
    }

    /**
     * @param imagenesPoaBean the imagenesPoaBean to set
     */
    public void setImagenesPoaBean(ImagenesPoaBean imagenesPoaBean) {
        this.imagenesPoaBean = imagenesPoaBean;
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
     * @return the proyectos
     */
    public LazyDataModel<Proyectospoa> getProyectospoa() {
        return proyectos;
    }

    /**
     * @param proyectos the proyectos to set
     */
    public void setProyectospoa(LazyDataModel<Proyectospoa> proyectos) {
        this.proyectos = proyectos;
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
     * @return the proyectoPadre
     */
    public Proyectospoa getProyectoPadre() {
        return proyectoPadre;
    }

    /**
     * @param proyectoPadre the proyectoPadre to set
     */
    public void setProyectoPadre(Proyectospoa proyectoPadre) {
        this.proyectoPadre = proyectoPadre;
    }

    /**
     * @return the proyectoSeleccionado
     */
    public Proyectospoa getProyectoSeleccionado() {
        return proyectoSeleccionado;
    }

    /**
     * @param proyectoSeleccionado the proyectoSeleccionado to set
     */
    public void setProyectoSeleccionado(Proyectospoa proyectoSeleccionado) {
        this.proyectoSeleccionado = proyectoSeleccionado;
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
     * @return the codigoAnterior
     */
    public String getCodigoAnterior() {
        return codigoAnterior;
    }

    /**
     * @param codigoAnterior the codigoAnterior to set
     */
    public void setCodigoAnterior(String codigoAnterior) {
        this.codigoAnterior = codigoAnterior;
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
     * @return the titulo
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * @param titulo the titulo to set
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * @return the tipoBuscar
     */
    public Integer getTipoBuscar() {
        return tipoBuscar;
    }

    /**
     * @param tipoBuscar the tipoBuscar to set
     */
    public void setTipoBuscar(Integer tipoBuscar) {
        this.tipoBuscar = tipoBuscar;
    }

    /**
     * @return the anio
     */
    public Integer getAnio() {
        return anio;
    }

    /**
     * @param anio the anio to set
     */
    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    /**
     * @return the anioActual
     */
    public int getAnioActual() {
        return anioActual;
    }

    /**
     * @param anioActual the anioActual to set
     */
    public void setAnioActual(int anioActual) {
        this.anioActual = anioActual;
    }

    /**
     * @return the longitud
     */
    public int getLongitud() {
        return longitud;
    }

    /**
     * @param longitud the longitud to set
     */
    public void setLongitud(int longitud) {
        this.longitud = longitud;
    }

    /**
     * @return the nivel
     */
    public int getNivel() {
        return nivel;
    }

    /**
     * @param nivel the nivel to set
     */
    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    /**
     * @return the ingreso
     */
    public String getIngreso() {
        return ingreso;
    }

    /**
     * @param ingreso the ingreso to set
     */
    public void setIngreso(String ingreso) {
        this.ingreso = ingreso;
    }

    /**
     * @return the imputable
     */
    public String getImputable() {
        return imputable;
    }

    /**
     * @param imputable the imputable to set
     */
    public void setImputable(String imputable) {
        this.imputable = imputable;
    }

    /**
     * @return the formularioRenombrar
     */
    public Formulario getFormularioRenombrar() {
        return formularioRenombrar;
    }

    /**
     * @param formularioRenombrar the formularioRenombrar to set
     */
    public void setFormularioRenombrar(Formulario formularioRenombrar) {
        this.formularioRenombrar = formularioRenombrar;
    }

    /**
     * @return the observacionReforma
     */
    public String getObservacionReforma() {
        return observacionReforma;
    }

    /**
     * @param observacionReforma the observacionReforma to set
     */
    public void setObservacionReforma(String observacionReforma) {
        this.observacionReforma = observacionReforma;
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
     * @return the codigosBean
     */
    public CodigosBean getCodigosBean() {
        return codigosBean;
    }

    /**
     * @param codigosBean the codigosBean to set
     */
    public void setCodigosBean(CodigosBean codigosBean) {
        this.codigosBean = codigosBean;
    }

    /**
     * @return the vigente
     */
    public Date getVigente() {
        return vigente;
    }

    /**
     * @param vigente the vigente to set
     */
    public void setVigente(Date vigente) {
        this.vigente = vigente;
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
     * @return the listaProyecto
     */
    public List<Proyectospoa> getListaProyecto() {
        return listaProyecto;
    }

    /**
     * @param listaProyecto the listaProyecto to set
     */
    public void setListaProyecto(List<Proyectospoa> listaProyecto) {
        this.listaProyecto = listaProyecto;
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
     * @return the proyectos
     */
    public LazyDataModel<Proyectospoa> getProyectos() {
        return proyectos;
    }

    /**
     * @param proyectos the proyectos to set
     */
    public void setProyectos(LazyDataModel<Proyectospoa> proyectos) {
        this.proyectos = proyectos;
    }

    /**
     * @return the nombreAnterior
     */
    public String getNombreAnterior() {
        return nombreAnterior;
    }

    /**
     * @param nombreAnterior the nombreAnterior to set
     */
    public void setNombreAnterior(String nombreAnterior) {
        this.nombreAnterior = nombreAnterior;
    }

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
     * @return the separador
     */
    public String getSeparador() {
        return separador;
    }

    /**
     * @param separador the separador to set
     */
    public void setSeparador(String separador) {
        this.separador = separador;
    }

    /**
     * @return the nivelProyecto
     */
    public Integer getNivelProyecto() {
        return nivelProyecto;
    }

    /**
     * @param nivelProyecto the nivelProyecto to set
     */
    public void setNivelProyecto(Integer nivelProyecto) {
        this.nivelProyecto = nivelProyecto;
    }
}
