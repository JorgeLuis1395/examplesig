/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.activos.sfccbdmq;

import com.ejb.sfcarchivos.ArchivosFacade;
import com.ejb.sfcarchivos.ImagenesFacade;
import com.entidades.sfcarchivos.Archivos;
import com.entidades.sfcarchivos.Imagenes;
import com.lowagie.text.DocumentException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
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
import javax.faces.application.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.ActivosFacade;
import org.utilitarios.sfccbdmq.Recurso;
import org.beans.sfccbdmq.CodigosFacade;
import org.beans.sfccbdmq.ConstatacionesFacade;
import org.beans.sfccbdmq.CustodiosFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.OficinasFacade;
import org.entidades.sfccbdmq.Activos;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Constataciones;
import org.entidades.sfccbdmq.Custodios;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Entidades;
import org.entidades.sfccbdmq.Oficinas;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.component.fileentry.FileEntry;
import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.icefaces.ace.component.fileentry.FileEntryResults;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.personal.sfccbdmq.DocumentoPDF;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.talento.sfccbdmq.EmpleadosBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "constatacionesBienes")
@ViewScoped
public class ConstatacionesBienesBean {

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{oficinasSfccbdmq}")
    private OficinasBean oficinasBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{constatacionessinedificio}")
    private ConstatacionesSinEdificioBean constacionesSinEdificio;
    @ManagedProperty(value = "#{activosSfccbdmq}")
    private ActivosBean activos;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadosBean;

    private Constataciones constatacion;
    private LazyDataModel<Constataciones> listaConstataciones;
    private List<Constataciones> listaConsta;
    private Formulario formulario = new Formulario();
    private Formulario formularioConfirmacion = new Formulario();
    private Formulario formularioCargar = new Formulario();
    private Formulario formularioBuscar = new Formulario();
    private Formulario formularioReporte = new Formulario();
    private Empleados empleado;
    private Oficinas ubicacion;
    private Codigos estadoBien;
    private Archivos archivoImagen;
    private Imagenes imagen;
    private List<Codigos> listaEsdatoBien;
    private List<String> listaErrores;
    private Perfiles perfil;
    private DocumentoPDF pdf;
    private Recurso reportepdf;
    private String estadoCons;
    private String administrativo;
    private Boolean verActas = false;
    private Boolean esAdministrativo;
    private Resource reporte;
    private boolean acta;
    private boolean foto;
    private Codigos estadoB;
    private String nombre;
    private String cedula;
    private String codigo;
    private String serie;
    private Integer anio;

    @EJB
    private ConstatacionesFacade ejbConstataciones;
    @EJB
    private CodigosFacade ejbCodigos;
    @EJB
    private OficinasFacade ejbOficinas;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private ImagenesFacade ejbimagenes;
    @EJB
    private ArchivosFacade ejbArchivo;
    @EJB
    private CustodiosFacade ejbCustodios;
    @EJB
    private ActivosFacade ejbActivos;

    public ConstatacionesBienesBean() {
        listaConstataciones = new LazyDataModel<Constataciones>() {
            @Override
            public List<Constataciones> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }

    @PostConstruct
    private void activar() {
        esAdministrativo = ejbCustodios.esAdministrativo(seguridadbean.getLogueado().getPin());
        if (!esAdministrativo) {
            empleado = seguridadbean.getLogueado().getEmpleados();
            verActas = false;
        }

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ConstatacionesVista";
        if (perfilString == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
        this.setPerfil(seguridadbean.traerPerfil(perfilString));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
        Calendar fechaAsiento = Calendar.getInstance();
        fechaAsiento.setTime(configuracionBean.getConfiguracion().getPvigente());
        anio = fechaAsiento.get(Calendar.YEAR);
        anio = anio - 1;
    }

    public List<Constataciones> carga(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
//        if (seguridadbean.getLogueado() == null) {
//            MensajesErrores.advertencia("Opción solo para usuarios con rol específico");
//            return null;
//        }
        Map parametros = new HashMap();
//        String where = "o.cicustodio is not null";
        String where = "o.id is not null";
        for (Map.Entry e : map.entrySet()) {
            String clave = (String) e.getKey();
            String valor = (String) e.getValue();
            where += " and upper(o." + clave + ") like :" + clave.replaceAll("\\.", "");
            parametros.put(clave.replaceAll("\\.", ""), valor.toUpperCase() + "%");
        }
        if (getEmpleadosBean() != null) {
            if (getEmpleadosBean().getEmpleadoSeleccionado() != null) {
                where += " and o.cicustodio=:nombre";
                parametros.put("nombre", getEmpleadosBean().getEmpleadoSeleccionado().getEntidad().getPin());
            }
        }
//        if (empleado != null) {
//            where += " and o.cicustodio=:cicustodio";
//            parametros.put("cicustodio", empleado.getEntidad().getPin());
//        }
        if (estadoCons != null) {
            if (estadoCons.equals("1")) {
                where += " and o.codigobien is not null and o.constatado=true";
            }
            if (estadoCons.equals("2")) {
                where += " and o.codigobien is not null and o.constatado=false";
            }
            if (estadoCons.equals("3")) {
                where += " and o.codigobien is null";
            }

        }
        if (getAnio() != null) {
            where += " and o.anioconstatacion=:anioconstatacion";
            parametros.put("anioconstatacion", anio);
        }
        parametros.put(";where", where);
        parametros.put(";orden", "o.id desc");
        int total = 0;
        try {
            total = ejbConstataciones.contar(parametros);

            int endIndex = i + pageSize;
            if (endIndex > total) {
                endIndex = total;
            }
            parametros.put(";inicial", i);
            parametros.put(";final", endIndex);
            listaConstataciones.setRowCount(total);
            List<Constataciones> lista = ejbConstataciones.encontarParametros(parametros);
            for (Constataciones c : lista) {
                Oficinas o = c.getUbicacion() != null ? ejbOficinas.find(c.getUbicacion()) : null;
                c.setNombreOficina(o != null ? o.getNombre() : "");
                c.setNombreEdificio(o != null ? o.getEdificio().getNombre() : "");
            }
            return lista;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ConstatacionesBienesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void exporta() {
        listaConsta = new LinkedList<>();
        Map parametros = new HashMap();
//        String where = "o.cicustodio is not null ";
        String where = "o.id is not null";
        if (getEmpleadosBean() != null) {
            if (getEmpleadosBean().getEmpleadoSeleccionado() != null) {
                where += " and o.cicustodio=:nombre";
                parametros.put("nombre", getEmpleadosBean().getEmpleadoSeleccionado().getEntidad().getPin());
            }
        }
        if (estadoCons != null) {
            if (estadoCons.equals("1")) {
                where += " and o.codigobien is not null and o.constatado=true";
            }
            if (estadoCons.equals("2")) {
                where += " and o.codigobien is not null and o.constatado=false";
            }
            if (estadoCons.equals("3")) {
                where += " and o.codigobien is null";
            }
        }
        if (getAnio() != null) {
            where += " and o.anioconstatacion=:anioconstatacion";
            parametros.put("anioconstatacion", anio);
        }
        parametros.put(";where", where);
        parametros.put(";orden", "o.id desc");
        try {
            listaConsta = ejbConstataciones.encontarParametros(parametros);
            for (Constataciones c : listaConsta) {
                Oficinas o = c.getUbicacion() != null ? ejbOficinas.find(c.getUbicacion()) : null;
                c.setNombreOficina(o != null ? o.getNombre() : "");
                c.setNombreEdificio(o != null ? o.getEdificio().getNombre() : "");
            }
            DocumentoXLS xls = new DocumentoXLS("Constataciones");
            List<AuxiliarReporte> campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "N°"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Cédula custodio"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Custodio"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Código de inventario"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Código del bien"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Inventario"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Descripción"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Serie"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Color"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Estado del bien"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Edificio"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Oficina"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Observaciones"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Constatado"));
            xls.agregarFila(campos, true);
            int fila = 1;
            for (Constataciones constat : listaConsta) {
                campos = new LinkedList<>();
                Empleados e = traerEmpleado(constat.getCicustodio());
                campos.add(new AuxiliarReporte("Integer", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, fila++));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, constat.getCicustodio() != null ? constat.getCicustodio() : ""));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, e != null ? e.toString() : ""));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, constat.getCodigo() != null ? constat.getCodigo() : ""));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, constat.getCodigobien() != null ? constat.getCodigobien() : ""));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, constat.getInventario() != null ? constat.getInventario() : ""));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, constat.getDescripcion() != null ? constat.getDescripcion() : ""));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, constat.getNroserie() != null ? constat.getNroserie() : ""));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, constat.getColor() != null ? constat.getColor() : ""));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, constat.getEstadobien() != null ? constat.getEstadobien() : ""));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, constat.getNombreEdificio()));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, constat.getNombreOficina()));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, constat.getObservacion() != null ? constat.getObservacion() : ""));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, constat.getConstatado() != null ? constat.getConstatado() ? "Constatado" : "No Constatado" : "Sobrante"));
                xls.agregarFila(campos, false);
            }
            reporte = xls.traerRecurso();
            formularioReporte.insertar();
        } catch (ConsultarException | IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ConstatacionesBienesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String buscar() {
        if (empleadosBean.getApellidos().isEmpty() || empleadosBean.getApellidos() == null) {
            empleadosBean.setEmpleadoSeleccionado(null);
        }
        listaConsta = new LinkedList<>();
        listaConstataciones = new LazyDataModel<Constataciones>() {
            @Override
            public List<Constataciones> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return carga(i, i1, scs, map);
            }
        };

//        if (empleado == null) {
//
//        } else {
//            exportar();
//        }
        return null;
    }

    public void cambiaEmpleado(ValueChangeEvent event) {
        empleado = (Empleados) event.getNewValue();
        try {
            if (empleado != null) {
                Map parametros = new HashMap();
                parametros.put(";where", "o.cicustodio=:ci");
                parametros.put("ci", empleado.getEntidad().getPin());
                if (ejbConstataciones.contar(parametros) > 0) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.cicustodio=:cicustodio and o.estado=false");
                    parametros.put("cicustodio", empleado.getEntidad().getPin());
                    verActas = ejbConstataciones.contar(parametros) == 0;
                } else {
                    verActas = false;
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " " + ex.getCause());
            Logger.getLogger(ConstatacionesBienesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public Recurso getConstatados() {
        buscarConstatacion(1);
        return reportepdf;
    }

    public Recurso getNoConstatados() {
        buscarConstatacion(0);
        return reportepdf;
    }

    public Recurso getSobrantes() {
        buscarConstatacion(2);
        return reportepdf;
    }

    private void buscarConstatacion(Integer constatado) {
        estadoCons = constatado.toString();
        Map parametros = new HashMap();
        String where;
        if (constatado != 2) {
            where = "o.cicustodio=:cicustodio and o.constatado=:estado";
            parametros.put("estado", (constatado != 0));
        } else {
            where = "o.cicustodio=:cicustodio and o.constatado is null";
        }

        parametros.put("cicustodio", empleado.getEntidad().getPin());

        parametros.put(";where", where);
        parametros.put(";orden", "o.id desc");
        try {
            listaConsta = ejbConstataciones.encontarParametros(parametros);
            for (Constataciones c : listaConsta) {
                Oficinas o = c.getUbicacion() != null ? ejbOficinas.find(c.getUbicacion()) : null;
                c.setNombreOficina(o != null ? o.getNombre() : "");
                c.setNombreEdificio(o != null ? o.getEdificio().getNombre() : "");
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ConstatacionesBienesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        reporteActaConstatados();
    }

    public String crear() {
        if (!esAdministrativo) {
            MensajesErrores.error("Opción sólo para custodios administrativos");
            return null;
        }
        if (empleado == null) {
            MensajesErrores.advertencia("Seleccione un custodio de bienes.");
            return null;
        }
        acta = false;
        foto = false;
        constatacion = new Constataciones();
        oficinasBean.setOficina(null);
        constatacion.setConstatado(null);
        constatacion.setEstado(Boolean.FALSE);
        formulario.insertar();
        archivoImagen = null;
        imagen = null;
        return null;
    }

    private void consultarImagenes() {
        Map parametros = new HashMap();
        parametros.put(";where", "o.imagen.modulo='CONSTATACIONFOTO' and o.imagen.idmodulo=:idmodulo");
        parametros.put("idmodulo", constatacion.getId());
        archivoImagen = null;
        imagen = null;
        List<Archivos> larch = ejbArchivo.encontarParametros(parametros);
        if (!larch.isEmpty()) {
            archivoImagen = larch.get(0);
            imagen = archivoImagen.getImagen();
        }
    }

    public String modificar() {
        if (!esAdministrativo) {
            MensajesErrores.advertencia("Opción sólo para custodios administrativos");
            return null;
        }
        constatacion = (Constataciones) listaConstataciones.getRowData();
        if (constatacion.getTipoarchivo() == null) {
            acta = false;
            foto = false;
        } else {
            if (constatacion.getTipoarchivo() == 1) {
                acta = true;
            }
            if (constatacion.getTipoarchivo() == 2) {
                foto = true;
            }

        }

        try {
            oficinasBean.setOficina(constatacion.getUbicacion() != null ? ejbOficinas.find(constatacion.getUbicacion()) : null);
            oficinasBean.setEdificio(oficinasBean.getOficina() != null ? oficinasBean.getOficina().getEdificio() : null);
        } catch (ConsultarException ex) {
            Logger.getLogger(ConstatacionesBienesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.editar();
        consultarImagenes();
        return null;
    }

    public String eliminar() {
        if (!esAdministrativo) {
            MensajesErrores.advertencia("Opción sólo para custodios administrativos");
            return null;
        }
        constatacion = (Constataciones) listaConstataciones.getRowData();
        try {
            oficinasBean.setOficina(constatacion.getUbicacion() != null ? ejbOficinas.find(constatacion.getUbicacion()) : null);
            oficinasBean.setEdificio(oficinasBean.getOficina() != null ? oficinasBean.getOficina().getEdificio() : null);
        } catch (ConsultarException ex) {
            Logger.getLogger(ConstatacionesBienesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.eliminar();
        return null;
    }

    private boolean validar() {
//        if (!formulario.isNuevo()) {
//            if (constatacion.getCodigobien() == null) {
//                MensajesErrores.advertencia("Ingrese codigo del bien");
//                return true;
//            }
//            if (constatacion.getCodigo() == null) {
//                MensajesErrores.advertencia("Ingrese codigo");
//                return true;
//            }
//        }
        if (constatacion.getDescripcion() == null) {
            MensajesErrores.advertencia("Ingrese descripción");
            return true;
        }
        if (constatacion.getNroserie() == null) {
            MensajesErrores.advertencia("Ingrese serie");
            return true;
        }
        if (estadoB == null) {
            MensajesErrores.advertencia("Seleccione estado del bien");
            return true;
        }
        if (oficinasBean.getOficina() == null) {
            MensajesErrores.advertencia("Seleccione oficina");
            return true;
        }
        if (constatacion.getObservacion() == null) {
            MensajesErrores.advertencia("Ingrese observación");
            return true;
        }
        if (archivoImagen == null) {
            MensajesErrores.advertencia("Ingrese imagen o acta");
            return true;
        }
        if (!acta && !foto) {
            MensajesErrores.advertencia("Selecione el tipo de Archivo");
            return true;
        }

        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        constatacion.setCicustodio(empleado.getEntidad().getPin());
        constatacion.setUbicacion(oficinasBean.getOficina().getId());
        constatacion.setFechaconstatacion(new Date());
        if (acta) {
            constatacion.setTipoarchivo(1);
        }
        if (foto) {
            constatacion.setTipoarchivo(2);
        }
        if (estadoB != null) {
            constatacion.setEstadobien(estadoB.getDescripcion());
        }
        try {
            ejbConstataciones.create(constatacion, seguridadbean.getLogueado().getUserid());
            if (archivoImagen != null) {
                imagen.setModulo("CONSTATACIONFOTO");
                imagen.setIdmodulo(constatacion.getId());
                ejbimagenes.create(imagen);
                archivoImagen.setImagen(imagen);
                ejbArchivo.create(archivoImagen);
            }

        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ConstatacionesBienesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String grabar() {
        if (validar()) {
            return null;
        }
        if (constatacion.getConstatado() == null) {
            constatacion.setConstatado(null);
        }
        if (acta) {
            constatacion.setTipoarchivo(1);
        }
        if (foto) {
            constatacion.setTipoarchivo(2);
        }
        if (estadoB != null) {
            constatacion.setEstadobien(estadoB.getDescripcion());
        }
        try {
            constatacion.setUbicacion(oficinasBean.getOficina().getId());
            ejbConstataciones.edit(constatacion, seguridadbean.getLogueado().getUserid());
            if (archivoImagen != null) {
                if (archivoImagen.getId() == null) {
                    imagen.setModulo("CONSTATACIONFOTO");
                    imagen.setIdmodulo(constatacion.getId());
                    ejbimagenes.create(imagen);
                    archivoImagen.setImagen(imagen);
                    ejbArchivo.create(archivoImagen);
                } else {
                    imagen.setModulo("CONSTATACIONFOTO");
                    imagen.setIdmodulo(constatacion.getId());
                    ejbimagenes.edit(imagen);
                    archivoImagen.setImagen(imagen);
                    ejbArchivo.edit(archivoImagen);
                }
            }
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ConstatacionesBienesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borrar() {
        try {
            ejbConstataciones.remove(constatacion, seguridadbean.getLogueado().getUserid());

            if (archivoImagen != null) {
                if (archivoImagen.getId() == null) {
                    imagen.setModulo("CONSTATACIONFOTO");
                    imagen.setIdmodulo(constatacion.getId());
                    ejbimagenes.create(imagen);
                    archivoImagen.setImagen(imagen);
                    ejbArchivo.remove(archivoImagen);
                } else {
                    imagen.setModulo("CONSTATACIONFOTO");
                    imagen.setIdmodulo(constatacion.getId());
                    ejbimagenes.edit(imagen);
                    archivoImagen.setImagen(imagen);
                    ejbArchivo.remove(archivoImagen);
                }
            }
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ConstatacionesBienesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String salir() {
        formulario.cancelar();
        return null;
    }

    public SelectItem[] getComboEstadoBien() {
        Map parametros = new HashMap();
        parametros.put(";where", "o.codigo='ESTACT'");
        parametros.put(";orden", "o.codigo asc");

        try {
            listaEsdatoBien = ejbCodigos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(ConstatacionesBienesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Combos.getSelectItems(listaEsdatoBien, true);
    }

    public Empleados traerEmpleado(String pin) {
        Map parametros = new HashMap();
        if (pin != null) {
            parametros.put(";where", "o.entidad.pin=:pin");
            parametros.put("pin", pin.trim());
        } else {
            parametros.put(";where", "o.entidad.pin is not null");
        }
        try {
            List<Empleados> listaEmple = ejbEmpleados.encontarParametros(parametros);
            for (Empleados c : listaEmple) {
                return c;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ConstatacionesBienesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String reporteActaConstatados() {
        try {

            Empleados e = traerEmpleado(administrativo);

            String organigrama = empleado.getOficina() != null ? empleado.getOficina().getOrganigrama().getNombre() : "";
            String nombreAdministrativo = e != null ? e.toString() : "";
            List<AuxiliarReporte> valores = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 8, true, "CÓDIGO DEL BIEN"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 8, true, "CÓDIGO"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 8, true, "DESCRIPCIÓN"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 8, true, "SERIE"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 8, true, "COLOR"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 8, true, "ESTADO"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 8, true, "UBICACIÓN"));
//            Calendar hoy = Calendar.getInstance();
//
//            if (hoy.get(Calendar.YEAR) > 2017) {
//                hoy.set(Calendar.YEAR, 2017);
//                hoy.set(Calendar.MONTH, 11);
//                hoy.set(Calendar.DAY_OF_MONTH, 31);
//            }

            SimpleDateFormat dia = new SimpleDateFormat("dd");
            SimpleDateFormat mes = new SimpleDateFormat("MM");
            SimpleDateFormat anio = new SimpleDateFormat("yyyy");
            String fecha = "";
            if (constatacion.getFechafinal() != null) {
                fecha = dia.format(constatacion.getFechafinal())
                        + " días del mes de " + nombremes(Integer.parseInt(mes.format(constatacion.getFechafinal())) - 1)
                        + " del " + anio.format(constatacion.getFechafinal());
            } else {
                constatacion.setFechafinal(new Date());
                fecha = dia.format(constatacion.getFechafinal())
                        + " días del mes de " + nombremes(Integer.parseInt(mes.format(constatacion.getFechafinal())) - 1)
                        + " del " + anio.format(constatacion.getFechafinal());
            }
            switch (estadoCons) {
                case "0"://No constatados
                    pdf = new DocumentoPDF("ACTIVOS FIJOS\nACTA DE BIENES NO CONSTATADOS (UBICADOS FÍSICAMENTE)", null, seguridadbean.getLogueado().getUserid());
                    pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
                    pdf.agregaParrafo(
                            "En la ciudad de Quito, a los "
                            //                            + hoy.get(Calendar.DAY_OF_MONTH)
                            //                            + " días del mes de " + nombremes(hoy.get(Calendar.MONTH))
                            //                            + " del " + hoy.get(Calendar.YEAR)
                            + fecha
                            + ", en la "
                            + organigrama + ", el Sr. "
                            + nombreAdministrativo + " como CUSTODIO ADMINISTRATIVO y el señor "
                            + empleado.toString()
                            + " como USUARIO FINAL dan fe de haber realizado la constatación física y que los bienes "
                            + "abajo descritos no han sido presentados quedando como BIENES NO CONSTATADOS, y "
                            + "quienes acuerdan suscribir la presente Acta de Bienes NO Constatados para dejar "
                            + "en constancia de haberse realizado el proceso respectivo:",
                            AuxiliarReporte.ALIGN_JUSTIFIED, 11, false);
                    pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
                    break;

                case "1"://Constatados
                    pdf = new DocumentoPDF("ACTIVOS FIJOS\nACTA DE BIENES CONSTATADOS", null, seguridadbean.getLogueado().getUserid());
                    pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
                    pdf.agregaParrafo(
                            "En la ciudad de Quito, a los "
                            //                            + hoy.get(Calendar.DAY_OF_MONTH)
                            //                            + " días del mes de " + nombremes(hoy.get(Calendar.MONTH))
                            //                            + " del " + hoy.get(Calendar.YEAR)
                            + fecha
                            + ", en la "
                            + organigrama + " se realiza la Constatación Física de los bienes de larga duración y sujetos de cotrol, para lo cual comparece el Sr. "
                            + nombreAdministrativo + " como CUSTODIO ADMINISTRATIVO y el señor "
                            + empleado.toString()
                            + " como USUARIO FINAL quienes acuerdan suscribir la presente Acta de Entrega Recepción para dejar en constancia de haberse "
                            + "constatado a entera satisfacción los bienes que a continuación se detallan: ",
                            AuxiliarReporte.ALIGN_JUSTIFIED, 11, false);
                    pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
                    break;

                case "2"://Sobrantes
                    pdf = new DocumentoPDF("ACTIVOS FIJOS\nACTA DE BIENES CONSTATADOS SOBRANTES", null, seguridadbean.getLogueado().getUserid());
                    pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
                    pdf.agregaParrafo(
                            "En la ciudad de Quito, a los "
                            //                            + hoy.get(Calendar.DAY_OF_MONTH)
                            //                            + " días del mes de " + nombremes(hoy.get(Calendar.MONTH))
                            //                            + " del " + hoy.get(Calendar.YEAR) 
                            + fecha
                            + ", en la "
                            + organigrama + " se realiza la Constatación Física de los bienes de larga duración y sujetos de cotrol, para lo cual comparece el Sr. "
                            + nombreAdministrativo + " como CUSTODIO ADMINISTRATIVO y el señor "
                            + empleado.toString()
                            + " como USUARIO FINAL quienes acuerdan suscribir la presente Acta de Entrega Recepción para dejar en constancia de los bienes "
                            + "sobrantes que se han encontrado en la respectiva constatación:  ",
                            AuxiliarReporte.ALIGN_JUSTIFIED, 11, false);
                    pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
                    break;
            }

            for (Constataciones c : listaConsta) {
                valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 8, false, c.getCodigobien() != null ? c.getCodigobien() : ""));
                valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 8, false, c.getCodigo() != null ? c.getCodigo() : ""));
                valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 8, false, c.getDescripcion() != null ? c.getDescripcion() : ""));
                valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 8, false, c.getNroserie() != null ? c.getNroserie() : ""));
                valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 8, false, c.getColor() != null ? c.getColor() : ""));
                valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 8, false, c.getEstadobien() != null ? c.getEstadobien() : ""));
                valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 8, false, c.getNombreOficina()));
            }

            pdf.agregarTablaReporte(titulos, valores, titulos.size(), 100, "");
            pdf.agregaParrafo("\n Total: " + listaConsta.size(), AuxiliarReporte.ALIGN_JUSTIFIED, 8, false);

            switch (estadoCons) {
                case "0": //No constatados
                    pdf.agregaParrafo(
                            "\n\n NOTA: EL USUARIO FINAL " + empleado.toString() + " TIENE EL PLAZO DE 72 HORAS PARA PRESENTAR LAS "
                            + "JUSTIFICACIONES DE LOS BIENES NO CONSTATADOS Y/O PROCEDER COMO DEMANDA EL Art. "
                            + "76-77-79 y 82 del acuerdo 041-CG reglamento de REGLAMENTO GENERAL PARA LA ADMINISTRACIÓN, "
                            + "UTILIZACIÓN, MANEJO Y CONTROL DE LOS BIENES Y EXISTENCIAS DEL SECTOR PÚBLICO)", AuxiliarReporte.ALIGN_JUSTIFIED, 8, false);
                    break;

                case "2"://Sobrantes
                    pdf.agregaParrafo(
                            "\n\n NOTA: Los bienes que han sido identificados como SOBRANTES serán trasladados a las bodegas de la "
                            + "Unidad de Bienes, para posteriormente ser entregados a los respectivos custodios de acuerdo "
                            + "a la normativa legal.", AuxiliarReporte.ALIGN_JUSTIFIED, 8, false);
                    break;
            }
            pdf.agregaParrafo("\n Custodio Administrativo.- Será el/la responsable de mantener "
                    + "actualizados los inventarios y registrar los ingresos, egresos y traspasos de los bienes en "
                    + "la unidad, conforme a las necesidades de los usuarios. El titular de cada unidad administrativa "
                    + "de la entidad u organismo, designará a los Custodios Administrativos, según la cantidad de "
                    + "bienes e inventarios de propiedad de la entidad u organismo y/o frecuencia de adquisición de "
                    + "los mismos. ", AuxiliarReporte.ALIGN_JUSTIFIED, 8, false);
            pdf.agregaParrafo("\n Usuario Final.- Será el/la responsable del cuidado, uso, custodia y conservación de "
                    + "los bienes asignados para el desempeño de sus funciones y los que por delegación expresa se "
                    + "agreguen a su cuidado.", AuxiliarReporte.ALIGN_JUSTIFIED, 8, false);
            pdf.agregaParrafo("\n Art. 3.- Del procedimiento y cuidado.- La máxima autoridad, a través de la unidad "
                    + "de administración de bienes o aquella que cumpliere este fin a nivel institucional, orientará "
                    + "y dirigirá la correcta conservación y cuidado de los bienes públicos  que han sido adquiridos "
                    + "o asignados para su uso en la entidad u organismo y que se hallen en su poder a cualquier "
                    + "título: depósito, custodia, préstamo de uso u otros semejantes, de acuerdo con este reglamento"
                    + " y las demás disposiciones que dicte la Contraloría General del Estado y la propia entidad "
                    + "u organismo.", AuxiliarReporte.ALIGN_JUSTIFIED, 8, false);
            pdf.agregaParrafo("\n Art. 10.- Constatación Física y Obligatoriedad de inventarios.En cada unidad "
                    + "administrativa se efectuará la constatación física de los bienes, por lo menos una vez al "
                    + "año, en el último trimestre, con el fin de controlar los inventarios en las entidades  u "
                    + "organismos y posibilitar los ajustes contables. En ella podrán intervenir el Guardalmacén "
                    + "o quien haga sus veces, el Custodio Administrativo y el titular de la Unidad Administrativa "
                    + "o su delegado. ", AuxiliarReporte.ALIGN_JUSTIFIED, 8, false);
            pdf.agregaParrafo("\n OBSERVACIÓN :", AuxiliarReporte.ALIGN_JUSTIFIED, 8, true);
            switch (estadoCons) {
                case "1"://Constatados
                case "2"://Sobrantes

                    pdf.agregaParrafo("\n Esta entrega recepción se sujeta a las siguientes cláusulas conforme establece el Reglamento General Sustitutivo "
                            + "para el Manejo y Administración de bienes del Sector Publico: ", AuxiliarReporte.ALIGN_JUSTIFIED, 8, false);
                    pdf.agregaParrafo("\n • Todos los bienes descritos en la presente Acta serán de exclusiva responsabilidad, buen uso y cuidado de quien (es) "
                            + "reciben.", AuxiliarReporte.ALIGN_JUSTIFIED, 8, false);
                    pdf.agregaParrafo("\n • En caso de cambio, retiro  o incremento de bienes, estos deberán ser notificados a la Unidad de Gestión de Bienes, "
                            + "para su actualización, registro y control.", AuxiliarReporte.ALIGN_JUSTIFIED, 8, false);
                    pdf.agregaParrafo("\n • En caso de perdida, deberá proceder conforme lo establece el Art. 79 del Acuerdo 41-CG",
                            AuxiliarReporte.ALIGN_JUSTIFIED, 8, true);
                    pdf.agregaParrafo("\n • En caso de establecer responsabilidad en su contra, la reposición de los bienes, se hará en dinero a precio de mercado o "
                            + "en especies de iguales características del bien desaparecido, destruido o inutilizado, conforme lo establece el Art. 77 del Acuerdo "
                            + "41-CG.",
                            AuxiliarReporte.ALIGN_JUSTIFIED, 8, false);
                    break;
                case "0"://No Constatados
                    pdf.agregaParrafo("\n Esta entrega recepción se sujeta a las siguientes cláusulas conforme establece el Reglamento "
                            + "General Sustitutivo para el Manejo y Administración de bienes del Sector Publico: ", AuxiliarReporte.ALIGN_JUSTIFIED, 8, false);
                    pdf.agregaParrafo("\n • Todos los bienes descritos en la presente Acta serán de exclusiva responsabilidad, buen uso y cuidado de quien (es) "
                            + "reciben.", AuxiliarReporte.ALIGN_JUSTIFIED, 8, false);
                    pdf.agregaParrafo("\n • En caso de cambio, retiro  o incremento de bienes, estos deberán ser notificados al la Unidad de Gestión de Bienes, "
                            + "para su actualización, registro y control.", AuxiliarReporte.ALIGN_JUSTIFIED, 8, false);
                    pdf.agregaParrafo("\n • En caso de perdida, deberá proceder conforme lo establece el Art. 79 del Acuerdo 41-CG. \"Art. 79.- "
                            + "Denuncia.- Cuando alguno de los bienes hubieren desaparecido, por hurto, robo, abigeato o por cualquier causa semejante, "
                            + "presunta, el Usuario Final o Custodio Administrativo encargado de su custodia, comunicará inmediatamente después de conocido "
                            + "el hecho por escrito el Guardalmacén o a quien haga sus veces, al jefe inmediato y a la máxima autoridad de la entidad u organismo o "
                            + "su delegado, con todos los pormenores que fueren del caso. La máxima autoridad o su delegado, dispondrá al Director Jurídico o quien "
                            + "haga sus veces de la entidad u organismo, formular de inmediato la denuncia correspondiente ante la Fiscalía General del Estado o "
                            + "Policía Nacional, de ser el caso, la cual deberá ser acompañada por los documentos que crediten la propiedad de los bienes presuntamente "
                            + "sustraídos. El Guardalmacén o quien haga sus veces y el Usuario Final o Custodio Administrativo, a petición del abogado que llevará la "
                            + "causa, facilitarán y entregarán la información necesaria para los trámites legales; el abogado será el responsable de impulsar la causa "
                            + "hasta la conclusión del proceso, de acuerdo a las formalidades establecidas en el Código Orgánico Integral Penal.\"",
                            AuxiliarReporte.ALIGN_JUSTIFIED, 8, true);
                    pdf.agregaParrafo("\n • En caso de establecer responsabilidad en su contra, la reposición de los bienes, se hará en dinero a precio de mercado o "
                            + "en especies de iguales características del bien desaparecido, destruido o inutilizado, conforme lo establece el Art. 77 del Acuerdo "
                            + "41-CG.",
                            AuxiliarReporte.ALIGN_JUSTIFIED, 8, false);
                    pdf.agregaParrafo("\n Art. 77.- Reposición de bienes, restitución de su valor o reemplazo del bien. Los bienes de propiedad de las entidades u organismos"
                            + " comprendidos en el artículo 1 del presente reglamento, que hubieren desaparecido, por robo, hurto, abigeato o semejantes; o, hubieren sufrido "
                            + "daños parciales o totales, y quedaren inutilizados; o, no hubieren sido presentados por el Usuario Final al momento de la constatación física "
                            + "o, en el momento de entrega recepción por cambio de Usuario Final o cesación de funciones; o, en caso de pérdida por negligencia o mal uso, "
                            + "deberán ser restituidos, o reemplazados por otros de acuerdo con las necesidades institucionales, por parte de los Usuarios Finales de los bienes. "
                            + "La reposición del bien se podrá llevar a cabo, en dinero, al precio actual de mercado, o con un bien de iguales características al bien desaparecido,"
                            + " destruido o inutilizado, previa autorización de la máxima autoridad o su delegado.",
                            AuxiliarReporte.ALIGN_JUSTIFIED, 8, false);
                    break;
            }

            pdf.agregaParrafo("\n\n", AuxiliarReporte.ALIGN_JUSTIFIED, 8, false);
            List<String> tit = new LinkedList<>();
            valores = new LinkedList<>();
            tit.add("CUSTODIO ADMINISTRATIVO");
            tit.add("USUARIO FINAL");
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 11, true, "C.I. " + administrativo));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 11, true, "C.I. " + empleado.getEntidad().getPin()));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 8, false, ""));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 8, false, ""));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 8, false, ""));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 8, false, ""));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 8, false, ""));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 8, false, ""));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 8, false, ""));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 8, false, ""));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 8, false, "____________________________"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 8, false, "____________________________"));
            pdf.agregarTabla(tit, valores, 2, 100, "", AuxiliarReporte.ALIGN_CENTER);

            pdf.agregaParrafo("\n\n", AuxiliarReporte.ALIGN_JUSTIFIED, 8, false);
            tit = new LinkedList<>();
            valores = new LinkedList<>();
            tit.add("DELEGADO DE LA UNIDAD");
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 11, true, ""));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 8, false, ""));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 8, false, ""));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 8, false, ""));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 8, false, ""));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 8, false, "____________________________"));
            pdf.agregarTabla(tit, valores, 1, 100, "", AuxiliarReporte.ALIGN_CENTER);

            reportepdf = pdf.traerRecurso();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ConstatacionesBienesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String multimediaListener(FileEntryEvent e) {
        FileEntry fe = (FileEntry) e.getComponent();
        FileEntryResults results = fe.getResults();
        for (FileEntryResults.FileInfo i : results.getFiles()) {
            try {
                if (archivoImagen == null) {
                    archivoImagen = new Archivos();
                }
                if (imagen == null) {
                    imagen = new Imagenes();
                }
                File file = i.getFile();
                archivoImagen.setArchivo(Files.readAllBytes(file.toPath()));
                imagen.setNombre(i.getFileName());
                imagen.setFechaingreso(new Date());
                imagen.setTipo(i.getContentType());
            } catch (IOException ex) {
                MensajesErrores.fatal(ex.getMessage() + ":" + ex.getCause());
                Logger.getLogger("").log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public String cargarDatos() {
        listaConsta = new LinkedList<>();
        constatacion = new Constataciones();
        formularioCargar.insertar();
        return null;
    }

    public String buscarConstatacion() {
        if (!esAdministrativo) {
            MensajesErrores.error("Opción sólo para custodios administrativos");
            return null;
        }
        if (!ejbCustodios.esAdministrativo(seguridadbean.getLogueado().getPin())) {
            MensajesErrores.error("Opción sólo para custodios administrativos");
            return null;
        }
        administrativo = ejbCustodios.traerAdministrativo(empleado.getEntidad().getPin());
        if (administrativo == null) {
            MensajesErrores.advertencia("Custodio administrativo no registrado");
            return null;
        }
        buscar();
        if (!listaConsta.isEmpty()) {
            constatacion = listaConsta.get(0);
        }
        formularioBuscar.insertar();
        return null;
    }

    public String nombremes(Integer i) {
        switch (i) {
            case 0:
                return "Enero";
            case 1:
                return "Febrero";
            case 2:
                return "Marzo";
            case 3:
                return "Abril";
            case 4:
                return "Mayo";
            case 5:
                return "Junio";
            case 6:
                return "Julio";
            case 7:
                return "Agosto";
            case 8:
                return "Septiembre";
            case 9:
                return "Octubre";
            case 10:
                return "Noviembre";
            case 11:
                return "Diciembre";
        }
        return "";
    }

    public String finalizar() {
        if (!esAdministrativo) {
            MensajesErrores.error("Opción sólo para custodios administrativos");
            return null;
        }
        if (empleado == null) {
            MensajesErrores.advertencia("Seleccione un custodio de bienes.");
            return null;
        }

        formularioConfirmacion.insertar();
        return null;
    }

    public String grabarFinalizar() {
        try {
            buscar();
            for (Constataciones c : listaConsta) {
//                c.setFechafinal(new Date());
                c.setFechafinal(configuracionBean.getConfiguracion().getPfinal());
                ejbConstataciones.edit(c, seguridadbean.getLogueado().getUserid());
            }
            ejbConstataciones.finalizar(empleado.getEntidad().getPin());
        } catch (InsertarException | GrabarException ex) {
            Logger.getLogger(ConstatacionesBienesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioConfirmacion.cancelar();
        buscar();
        verActas = true;
        return null;
    }

    public String nombreArchivo() {
        Calendar c = Calendar.getInstance();
        return "" + c.getTimeInMillis();
    }

//    public String exportar() {
//        //formularioReporte.insertar();
//        try {
//            Map parametros = new HashMap();
//            String where = "o.cicustodio is not null";
//            if (estadoCons != null) {
//                if (!"2".equals(estadoCons)) {
//                    where += " and o.constatado=:estado";
//                    parametros.put("estado", (!"0".equals(estadoCons)));
//                } else {
//                    where += " and o.constatado is null";
//                }
//            }
//            parametros.put(";where", where);
//            parametros.put(";orden", "o.id desc");
//            try {
//                
//            } catch (ConsultarException ex) {
//                Logger.getLogger(ConstatacionesBienesBean.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        } catch (IOException | DocumentException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(ConstatacionesBienesBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }
    public SelectItem[] getComboCustodioBienes() {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.ciadministrativo=:administrativo");
            parametros.put("administrativo", seguridadbean.getLogueado().getPin());
            List<Custodios> lista = ejbCustodios.encontarParametros(parametros);
            List<Empleados> retorno = new LinkedList<>();
            for (Custodios c : lista) {
                Entidades e = seguridadbean.traerCedula(c.getCibien());

                if (e != null) {
                    boolean finalizado;
                    parametros = new HashMap();
                    parametros.put(";where", "o.cicustodio=:ci");
                    parametros.put("ci", e.getPin());
                    if (ejbConstataciones.contar(parametros) == 0) {
                        finalizado = false;
                    } else {
                        parametros = new HashMap();
                        parametros.put(";where", "o.cicustodio=:ci and o.estado=false");
                        parametros.put("ci", e.getPin());
                        finalizado = ejbConstataciones.contar(parametros) == 0;
                    }
                    e.setNombres(e.getNombres() + (finalizado ? " (finalizado)" : ""));
                    retorno.add(e.getEmpleados());
                }
            }
            return Combos.getSelectItems(retorno, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " " + ex.getCause());
            Logger.getLogger(ConstatacionesBienesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the constatacion
     */
    public Constataciones getConstatacion() {
        return constatacion;
    }

    /**
     * @param constatacion the constatacion to set
     */
    public void setConstatacion(Constataciones constatacion) {
        this.constatacion = constatacion;
    }

    /**
     * @return the listaConstataciones
     */
    public LazyDataModel<Constataciones> getListaConstataciones() {
        return listaConstataciones;
    }

    /**
     * @param listaConstataciones the listaConstataciones to set
     */
    public void setListaConstataciones(LazyDataModel<Constataciones> listaConstataciones) {
        this.listaConstataciones = listaConstataciones;
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
     * @return the estadoBien
     */
    public Codigos getEstadoBien() {
        return estadoBien;
    }

    /**
     * @param estadoBien the estadoBien to set
     */
    public void setEstadoBien(Codigos estadoBien) {
        this.estadoBien = estadoBien;
    }

    /**
     * @return the ubicacion
     */
    public Oficinas getUbicacion() {
        return ubicacion;
    }

    /**
     * @param ubicacion the ubicacion to set
     */
    public void setUbicacion(Oficinas ubicacion) {
        this.ubicacion = ubicacion;
    }

    /**
     * @return the listaEsdatoBien
     */
    public List<Codigos> getListaEsdatoBien() {
        return listaEsdatoBien;
    }

    /**
     * @param listaEsdatoBien the listaEsdatoBien to set
     */
    public void setListaEsdatoBien(List<Codigos> listaEsdatoBien) {
        this.listaEsdatoBien = listaEsdatoBien;
    }

    /**
     * @return the listaErrores
     */
    public List<String> getListaErrores() {
        return listaErrores;
    }

    /**
     * @param listaErrores the listaErrores to set
     */
    public void setListaErrores(List<String> listaErrores) {
        this.listaErrores = listaErrores;
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
     * @return the formularioConfirmacion
     */
    public Formulario getFormularioConfirmacion() {
        return formularioConfirmacion;
    }

    /**
     * @param formularioConfirmacion the formularioConfirmacion to set
     */
    public void setFormularioConfirmacion(Formulario formularioConfirmacion) {
        this.formularioConfirmacion = formularioConfirmacion;
    }

    /**
     * @return the pdf
     */
    public DocumentoPDF getPdf() {
        return pdf;
    }

    /**
     * @param pdf the pdf to set
     */
    public void setPdf(DocumentoPDF pdf) {
        this.pdf = pdf;
    }

    /**
     * @return the reportepdf
     */
    public Recurso getReportepdf() {
        return reportepdf;
    }

    /**
     * @param reportepdf the reportepdf to set
     */
    public void setReportepdf(Recurso reportepdf) {
        this.reportepdf = reportepdf;
    }

    /**
     * @return the listaConsta
     */
    public List<Constataciones> getListaConsta() {
        return listaConsta;
    }

    /**
     * @param listaConsta the listaConsta to set
     */
    public void setListaConsta(List<Constataciones> listaConsta) {
        this.listaConsta = listaConsta;
    }

    /**
     * @return the estadoCons
     */
    public String getEstadoCons() {
        return estadoCons;
    }

    /**
     * @param estadoCons the estadoCons to set
     */
    public void setEstadoCons(String estadoCons) {
        this.estadoCons = estadoCons;
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

    /**
     * @return the formularioCargar
     */
    public Formulario getFormularioCargar() {
        return formularioCargar;
    }

    /**
     * @param formularioCargar the formularioCargar to set
     */
    public void setFormularioCargar(Formulario formularioCargar) {
        this.formularioCargar = formularioCargar;
    }

    /**
     * @return the formularioBuscar
     */
    public Formulario getFormularioBuscar() {
        return formularioBuscar;
    }

    /**
     * @param formularioBuscar the formularioBuscar to set
     */
    public void setFormularioBuscar(Formulario formularioBuscar) {
        this.formularioBuscar = formularioBuscar;
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
     * @return the oficinasBean
     */
    public OficinasBean getOficinasBean() {
        return oficinasBean;
    }

    /**
     * @param oficinasBean the oficinasBean to set
     */
    public void setOficinasBean(OficinasBean oficinasBean) {
        this.oficinasBean = oficinasBean;
    }

    /**
     * @return the verActas
     */
    public Boolean getVerActas() {
        return verActas;
    }

    /**
     * @param verActas the verActas to set
     */
    public void setVerActas(Boolean verActas) {
        this.verActas = verActas;
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
     * @return the formularioReporte
     */
    public Formulario getFormularioReporte() {
        return formularioReporte;
    }

    /**
     * @param formularioReporte the formularioReporte to set
     */
    public void setFormularioReporte(Formulario formularioReporte) {
        this.formularioReporte = formularioReporte;
    }

    /**
     * @return the esAdministrativo
     */
    public Boolean getEsAdministrativo() {
        return esAdministrativo;
    }

    /**
     * @param esAdministrativo the esAdministrativo to set
     */
    public void setEsAdministrativo(Boolean esAdministrativo) {
        this.esAdministrativo = esAdministrativo;
    }

    /**
     * @return the acta
     */
    public boolean isActa() {
        return acta;
    }

    /**
     * @param acta the acta to set
     */
    public void setActa(boolean acta) {
        this.acta = acta;
    }

    /**
     * @return the foto
     */
    public boolean isFoto() {
        return foto;
    }

    /**
     * @param foto the foto to set
     */
    public void setFoto(boolean foto) {
        this.foto = foto;
    }

    /**
     * @return the estadoB
     */
    public Codigos getEstadoB() {
        return estadoB;
    }

    /**
     * @param estadoB the estadoB to set
     */
    public void setEstadoB(Codigos estadoB) {
        this.estadoB = estadoB;
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
     * @return the serie
     */
    public String getSerie() {
        return serie;
    }

    /**
     * @param serie the serie to set
     */
    public void setSerie(String serie) {
        this.serie = serie;
    }

    /**
     * @return the constacionesSinEdificio
     */
    public ConstatacionesSinEdificioBean getConstacionesSinEdificio() {
        return constacionesSinEdificio;
    }

    /**
     * @param constacionesSinEdificio the constacionesSinEdificio to set
     */
    public void setConstacionesSinEdificio(ConstatacionesSinEdificioBean constacionesSinEdificio) {
        this.constacionesSinEdificio = constacionesSinEdificio;
    }

    /**
     * @return the activos
     */
    public ActivosBean getActivos() {
        return activos;
    }

    /**
     * @param activos the activos to set
     */
    public void setActivos(ActivosBean activos) {
        this.activos = activos;
    }

    /**
     * @return the empleadosBean
     */
    public EmpleadosBean getEmpleadosBean() {
        return empleadosBean;
    }

    /**
     * @param empleadosBean the empleadosBean to set
     */
    public void setEmpleadosBean(EmpleadosBean empleadosBean) {
        this.empleadosBean = empleadosBean;
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
}
