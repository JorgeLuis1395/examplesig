/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.activos.sfccbdmq;

import com.ejb.sfcarchivos.ArchivosFacade;
import com.entidades.sfcarchivos.Archivos;
import org.compras.sfccbdmq.ProveedoresBean;
import org.presupuestos.sfccbdmq.CargarAsignacionesBean;
import org.presupuestos.sfccbdmq.ClasificadorBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.Reportesds;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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
import org.beans.sfccbdmq.ActivosFacade;
import org.beans.sfccbdmq.CodigosFacade;
import org.beans.sfccbdmq.DetalletomaFacade;
import org.beans.sfccbdmq.EntidadesFacade;
import org.beans.sfccbdmq.OficinasFacade;
import org.beans.sfccbdmq.TomafisicaFacade;
import org.entidades.sfccbdmq.Activos;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Detalletoma;
import org.entidades.sfccbdmq.Edificios;
import org.entidades.sfccbdmq.Entidades;
import org.entidades.sfccbdmq.Externos;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Grupoactivos;
import org.entidades.sfccbdmq.Marcas;
import org.entidades.sfccbdmq.Oficinas;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Polizas;
import org.entidades.sfccbdmq.Tipoactivo;
import org.entidades.sfccbdmq.Tomafisica;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.icefaces.ace.component.fileentry.FileEntry;
import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.icefaces.ace.component.fileentry.FileEntryResults;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "tomaFisicaActivosSfccbdmq")
@ViewScoped
public class TomaFisicaBean {

    /**
     * Creates a new instance of ActivoBean
     */
    public TomaFisicaBean() {
        listaTomas = new LazyDataModel<Tomafisica>() {

            @Override
            public List<Tomafisica> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }

    @EJB
    private ActivosFacade ejbActivos;
    @EJB
    private EntidadesFacade ejbEntidades;
    @EJB
    private ArchivosFacade ejbArchivo;
    @EJB
    private OficinasFacade ejbOficina;
    @EJB
    private CodigosFacade ejbCodigos;
    @EJB
    private DetalletomaFacade ejbDetalleToma;
    @EJB
    private TomafisicaFacade ejbToma;
    private Archivos archivoImagen;
//    private int tamano;
    private String codigoAnterior;
    private Formatos nivel;
    private Activos activo;
    private Formulario formulario = new Formulario();
    private Formulario formularioToma = new Formulario();
    private Formulario formularioBuscar = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private LazyDataModel<Tomafisica> listaTomas;
    private List<Detalletoma> listaDetalleToma;
    private List<Detalletoma> listaNuevos;
    private List<Detalletoma> listaVerificados;
    private List<Detalletoma> listaDiferencias;
    private Tomafisica toma;
    private Detalletoma detalleToma;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String inventario;
    private String alterno;
//    private String marca;
    private Marcas marca;
    private String modelo;
    private String numeroserie;
    private String codigobarras;
    private String externo;
    private Tipoactivo tipo;
    private Grupoactivos grupo;
    private Codigos clasificacion;
    private Codigos estado;
    private Codigos estadoNuevo;
    private double valorAnterior;
    private Oficinas oficina;
    private Oficinas oficinaNueva;
    private Polizas poliza;
    private Edificios edificio;
    private Externos externoEntidad = new Externos();
    private Entidades entidad;
    private String apellidos;
    private Date desde;
    private Date hasta;
    private List listaUsuarios;
    private boolean esExterno = false;
    private int garantia;
    private int operativo;
    private int control;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{clasificadorSfccbdmq}")
    private ClasificadorBean clasificadorBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedoresBean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;
    private Perfiles perfil;
    @ManagedProperty(value = "#{oficinasSfccbdmq}")
    private OficinasBean oficinasBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    @ManagedProperty(value = "#{subGruposActivosSfccbdmq}")
    private SubGruposBean subgrupoBean;
    private String separador = ",";
    private Resource reporte;

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

    @PostConstruct
    private void activar() {
        hasta = configuracionBean.getConfiguracion().getPfinal();
        desde = configuracionBean.getConfiguracion().getPinicial();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "TomaFisicaActivosVista";
        if (perfil == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
        this.setPerfil(seguridadbean.traerPerfil(perfil));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(seguridadbean.getGrpUsuario().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                seguridadbean.cerraSession();
//            }
//        }
//    }
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
     * @return the nivel
     */
    public Formatos getNivel() {
        return nivel;
    }

    /**
     * @param nivel the nivel to set
     */
    public void setNivel(Formatos nivel) {
        this.nivel = nivel;
    }

    /**
     * @return the activo
     */
    public Activos getActivo() {
        return activo;
    }

    /**
     * @param activo the activo to set
     */
    public void setActivo(Activos activo) {
        this.activo = activo;
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

    public String buscar() {
        listaTomas = new LazyDataModel<Tomafisica>() {

            @Override
            public List<Tomafisica> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                if (scs.length == 0) {
                    parametros.put(";orden", "o.fecha");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }
                String where = "  o.fecha between :desde and :hasta";
                parametros.put("desde", getDesde());
                parametros.put("hasta", getHasta());
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();
                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }

                int total = 0;

                try {
                    parametros.put(";where", where);
                    total = ejbToma.contar(parametros);
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
                listaTomas.setRowCount(total);
                try {
                    return ejbToma.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        return null;
    }

    public String buscarActivos() {
        Map parametros = new HashMap();
        parametros.put(";orden", "o.activo.barras");
        String where = "";

        if (!((codigo == null) || (codigo.isEmpty()))) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += "  o.activo.codigo like :codigo";
            parametros.put("codigo", codigo + "%");
        }
        if (!((nombre == null) || (nombre.isEmpty()))) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += " upper(o.activo.nombre) like :nombre";
            parametros.put("nombre", nombre.toUpperCase() + "%");
        }
//        if (!((marca == null) || (marca.isEmpty()))) {
//            if (!where.isEmpty()) {
//                where += " and ";
//            }
//            where += "  upper(o.activo.marca) like :marca";
//            parametros.put("marca", marca.toUpperCase() + "%");
//        }
        if (!(marca == null)) {
            where += " and o.activomarca=:activomarca";
            parametros.put("activomarca", marca);
        }
        if (!((descripcion == null) || (descripcion.isEmpty()))) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += "  upper(o.activo.descripcion) like :descripcion";
            parametros.put("descripcion", descripcion.toUpperCase() + "%");
        }
        if (!((inventario == null) || (inventario.isEmpty()))) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += " upper(o.activo.inventario) like :inventario";
            parametros.put("inventario", inventario.toUpperCase() + "%");
        }
        if (!((alterno == null) || (alterno.isEmpty()))) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += "  upper(o.activo.alterno) like :alterno";
            parametros.put("alterno", alterno.toUpperCase() + "%");
        }
        if (!((codigobarras == null) || (codigobarras.isEmpty()))) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += "  upper(o.activo.barras) like :codigobarras";
            parametros.put("codigobarras", codigobarras.toUpperCase() + "%");
        }
        if (!((modelo == null) || (modelo.isEmpty()))) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += " upper(o.activo.modelo) like :modelo";
            parametros.put("modelo", modelo.toUpperCase() + "%");
        }
        if (!((numeroserie == null) || (numeroserie.isEmpty()))) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += " upper(o.activo.numeroserie) like :numeroserie";
            parametros.put("numeroserie", numeroserie.toUpperCase() + "%");
        }
        if (subgrupoBean.getGrupo() != null) {
            if (subgrupoBean.getSubGrupo() != null) {
                if (!where.isEmpty()) {
                    where += " and ";
                }
                where += " o.activo.subgrupo=:subgrupo";
                parametros.put("subgrupo", subgrupoBean.getSubGrupo());
            } else {
                if (!where.isEmpty()) {
                    where += " and ";
                }
                where += " o.activo.grupo=:grupo";
                parametros.put("grupo", subgrupoBean.getGrupo());
            }
        }
        if (tipo != null) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += " o.activo.grupo.tipo=:tipo";
            parametros.put("tipo", tipo);
        }
        if (estado != null) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += " o.activo.estado=:estado";
            parametros.put("estado", estado);
        }
        if (clasificacion != null) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += " o.activo.clasificacion=:clasificacion";
            parametros.put("clasificacion", clasificacion);
        }
        if (poliza != null) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += " o.activo.poliza=:poliza";
            parametros.put("poliza", poliza);
        }
        if (proveedoresBean.getEmpresa() != null) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += " o.activo.proveedor=:proveedor";
            parametros.put("proveedor", proveedoresBean.getEmpresa().getProveedores());
        }
        if (entidadesBean.getEntidad() != null) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += " o.activo.custodio=:custodio";
            parametros.put("custodio", entidadesBean.getEntidad().getEmpleados());
        }
        if (getOficinasBean().getEdificio() != null) {
            if (oficina != null) {
                if (!where.isEmpty()) {
                    where += " and ";
                }
                where += " o.activo.localizacion=:localizacion";
                parametros.put("localizacion", oficina);
            } else {
                if (!where.isEmpty()) {
                    where += " and ";
                }
                where += " o.activo.localizacion.edificio=:edificio";
                parametros.put("edificio", getOficinasBean().getEdificio());
            }
        }
        if (garantia == 1) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += " o.activo.garantia=true ";
        } else if (garantia == -1) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += " o.activo.garantia=false ";
        }
        if (operativo == 1) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += " o.activo.operativo=true ";
        } else if (operativo == -1) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += " o.activo.operativo=false ";
        }
        if (control == 1) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += " o.activo.control=true ";
        } else if (control == -1) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += " o.activo.control=false ";
        }

        try {
            parametros.put(";where", where);
            listaDetalleToma = ejbDetalleToma.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String imprimir() {
        Map parametros = new HashMap();
        parametros.put(";orden", "o.custodio.id");
        String where = "";

        if (!((codigo == null) || (codigo.isEmpty()))) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += "  o.codigo like :codigo";
            parametros.put("codigo", codigo + "%");
        }
        if (!((nombre == null) || (nombre.isEmpty()))) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += " upper(o.nombre) like :nombre";
            parametros.put("nombre", nombre.toUpperCase() + "%");
        }
//        if (!((marca == null) || (marca.isEmpty()))) {
//            if (!where.isEmpty()) {
//                where += " and ";
//            }
//            where += "  upper(o.marca) like :marca";
//            parametros.put("marca", marca.toUpperCase() + "%");
//        }
        if (!(marca == null)) {
            where += " and o.activomarca=:activomarca";
            parametros.put("activomarca", marca);
        }
        if (!((descripcion == null) || (descripcion.isEmpty()))) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += "  upper(o.descripcion) like :descripcion";
            parametros.put("descripcion", descripcion.toUpperCase() + "%");
        }
        if (!((inventario == null) || (inventario.isEmpty()))) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += " upper(o.inventario) like :inventario";
            parametros.put("inventario", inventario.toUpperCase() + "%");
        }
        if (!((alterno == null) || (alterno.isEmpty()))) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += "  upper(o.alterno) like :alterno";
            parametros.put("alterno", alterno.toUpperCase() + "%");
        }
        if (!((codigobarras == null) || (codigobarras.isEmpty()))) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += "  upper(o.barras) like :codigobarras";
            parametros.put("codigobarras", codigobarras.toUpperCase() + "%");
        }
        if (!((modelo == null) || (modelo.isEmpty()))) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += " upper(o.modelo) like :modelo";
            parametros.put("modelo", modelo.toUpperCase() + "%");
        }
        if (!((numeroserie == null) || (numeroserie.isEmpty()))) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += " upper(o.numeroserie) like :numeroserie";
            parametros.put("numeroserie", numeroserie.toUpperCase() + "%");
        }
        if (subgrupoBean.getGrupo() != null) {
            if (subgrupoBean.getSubGrupo() != null) {
                if (!where.isEmpty()) {
                    where += " and ";
                }
                where += " o.subgrupo=:subgrupo";
                parametros.put("subgrupo", subgrupoBean.getSubGrupo());
            } else {
                if (!where.isEmpty()) {
                    where += " and ";
                }
                where += " o.grupo=:grupo";
                parametros.put("grupo", subgrupoBean.getGrupo());
            }
        }
        if (tipo != null) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += " o.grupo.tipo=:tipo";
            parametros.put("tipo", tipo);
        }
        if (estado != null) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += " o.estado=:estado";
            parametros.put("estado", estado);
        }
        if (clasificacion != null) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += " o.clasificacion=:clasificacion";
            parametros.put("clasificacion", clasificacion);
        }

        if (proveedoresBean.getEmpresa() != null) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += " o.proveedor=:proveedor";
            parametros.put("proveedor", proveedoresBean.getEmpresa().getProveedores());
        }
        if (entidadesBean.getEntidad() != null) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += " o.custodio=:custodio";
            parametros.put("custodio", entidadesBean.getEntidad().getEmpleados());
        }
        if (getOficinasBean().getEdificio() != null) {
            if (oficina != null) {
                if (!where.isEmpty()) {
                    where += " and ";
                }
                where += " o.localizacion=:localizacion";
                parametros.put("localizacion", oficina);
            } else {
                if (!where.isEmpty()) {
                    where += " and ";
                }
                where += " o.localizacion.edificio=:edificio";
                parametros.put("edificio", getOficinasBean().getEdificio());
            }
        }
        if (garantia == 1) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += " o.garantia=true ";
        } else if (garantia == -1) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += " o.garantia=false ";
        }
        if (operativo == 1) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += " o.operativo=true ";
        } else if (operativo == -1) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += " o.operativo=false ";
        }
        if (control == 1) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += " o.control=true ";
        } else if (control == -1) {
            if (!where.isEmpty()) {
                where += " and ";
            }
            where += " o.control=false ";
        }

        try {
            parametros.put(";where", where);
            List<Activos> listaActivos = ejbActivos.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
            parametros.put("expresado", "REPORTE DE INVENTARIO FISICO DE BIENES ");
//        parametros.put("tipo", "REPORTE DE BIENES  ");
            parametros.put("nombrelogo", "logo-new.png");
            parametros.put("usuario", seguridadbean.getLogueado().getUserid());
            parametros.put("fecha", new Date());
            Calendar c = Calendar.getInstance();
            setReporte(new Reportesds(parametros,
                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/BienesCustodio.jasper"),
                    listaActivos, "Bienes" + String.valueOf(c.getTimeInMillis()) + ".pdf"));
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        formularioImprimir.editar();
        return null;
    }

    public String nuevo() {
        Map parametros = new HashMap();
        parametros.put(";where", "o.fecha=:fecha");
        parametros.put("fecha", new Date());
        try {
            int total = ejbToma.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Ya existe toma física para este día");
                return null;
            }
            parametros = new HashMap();
            parametros.put(";where", "o.fechafin is null");
            total = ejbToma.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Existen tomas físicas no terminadas no puede iniciar una nueva");
                return null;
            }

            toma = new Tomafisica();
            toma.setFecha(new Date());
            ejbToma.create(toma, getSeguridadbean().getLogueado().getUserid());
            ejbToma.generaToma(toma);
            listaDiferencias = new LinkedList<>();
            listaNuevos = new LinkedList<>();
            listaVerificados = new LinkedList<>();
        } catch (InsertarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(TomaFisicaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioToma.insertar();
        return null;
    }

    public String modifica() {
        toma = (Tomafisica) listaTomas.getRowData();
        Map parametros = new HashMap();
        parametros.put(";orden", "o.activo.barras");
        parametros.put(";where", "o.toma=:toma");
        parametros.put("toma", toma);
        listaDiferencias = new LinkedList<>();
        listaNuevos = new LinkedList<>();
        listaVerificados = new LinkedList<>();
        try {

            listaDetalleToma = new LinkedList<>();
            List<Detalletoma> listaD = ejbDetalleToma.encontarParametros(parametros);
            if (listaD.isEmpty()) {
                ejbToma.generaToma(toma);
                listaD = ejbDetalleToma.encontarParametros(parametros);
            }
            int fila = 0;
            for (Detalletoma d : listaD) {
                if ((d.getEscustodio()) || (d.getEsestado()) || (d.getEslocalidad())) {
                    listaDetalleToma.add(d);
//                    listaDiferencias.add(d);
                } else if (d.getActivo() == null) {
                    listaNuevos.add(d);
                } else if (d.getVerificado()) {
                    listaVerificados.add(d);
//                    listaDetalleToma.add(d);
                }
                fila++;
                listaDetalleToma.add(d);
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(TomaFisicaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioToma.editar();
        return null;
    }

    public String terminar() {

        try {
            toma.setFechafin(new Date());
            ejbToma.edit(toma, getSeguridadbean().getLogueado().getUserid());

        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        listaDetalleToma = new LinkedList<>();
        listaDiferencias = new LinkedList<>();
        listaNuevos = new LinkedList<>();
        listaVerificados = new LinkedList<>();
        formularioToma.cancelar();
        buscar();
        return null;
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
     * @return the clasificadorBean
     */
    public ClasificadorBean getClasificadorBean() {
        return clasificadorBean;
    }

    /**
     * @param clasificadorBean the clasificadorBean to set
     */
    public void setClasificadorBean(ClasificadorBean clasificadorBean) {
        this.clasificadorBean = clasificadorBean;
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

    public Activos traer(Integer id) throws ConsultarException {
        return ejbActivos.find(id);
    }

    /**
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * @return the inventario
     */
    public String getInventario() {
        return inventario;
    }

    /**
     * @param inventario the inventario to set
     */
    public void setInventario(String inventario) {
        this.inventario = inventario;
    }

    /**
     * @return the alterno
     */
    public String getAlterno() {
        return alterno;
    }

    /**
     * @param alterno the alterno to set
     */
    public void setAlterno(String alterno) {
        this.alterno = alterno;
    }

    /**
     * @return the modelo
     */
    public String getModelo() {
        return modelo;
    }

    /**
     * @param modelo the modelo to set
     */
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    /**
     * @return the numeroserie
     */
    public String getNumeroserie() {
        return numeroserie;
    }

    /**
     * @param numeroserie the numeroserie to set
     */
    public void setNumeroserie(String numeroserie) {
        this.numeroserie = numeroserie;
    }

    /**
     * @return the codigobarras
     */
    public String getCodigobarras() {
        return codigobarras;
    }

    /**
     * @param codigobarras the codigobarras to set
     */
    public void setCodigobarras(String codigobarras) {
        this.codigobarras = codigobarras;
    }

    /**
     * @return the externo
     */
    public String getExterno() {
        return externo;
    }

    /**
     * @param externo the externo to set
     */
    public void setExterno(String externo) {
        this.externo = externo;
    }

    /**
     * @return the tipo
     */
    public Tipoactivo getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(Tipoactivo tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the clasificacion
     */
    public Codigos getClasificacion() {
        return clasificacion;
    }

    /**
     * @param clasificacion the clasificacion to set
     */
    public void setClasificacion(Codigos clasificacion) {
        this.clasificacion = clasificacion;
    }

    /**
     * @return the estado
     */
    public Codigos getEstado() {
        return estado;
    }

    /**
     * @param estado the estado to set
     */
    public void setEstado(Codigos estado) {
        this.estado = estado;
    }

    /**
     * @return the oficina
     */
    public Oficinas getOficina() {
        return oficina;
    }

    /**
     * @param oficina the oficina to set
     */
    public void setOficina(Oficinas oficina) {
        this.oficina = oficina;
    }

    /**
     * @return the edificio
     */
    public Edificios getEdificio() {
        return edificio;
    }

    /**
     * @param edificio the edificio to set
     */
    public void setEdificio(Edificios edificio) {
        this.edificio = edificio;
    }

    /**
     * @return the proveedoresBean
     */
    public ProveedoresBean getProveedoresBean() {
        return proveedoresBean;
    }

    /**
     * @param proveedoresBean the proveedoresBean to set
     */
    public void setProveedoresBean(ProveedoresBean proveedoresBean) {
        this.proveedoresBean = proveedoresBean;
    }

    /**
     * @return the entidadesBean
     */
    public EntidadesBean getEntidadesBean() {
        return entidadesBean;
    }

    /**
     * @param entidadesBean the entidadesBean to set
     */
    public void setEntidadesBean(EntidadesBean entidadesBean) {
        this.entidadesBean = entidadesBean;
    }

    /**
     * @return the grupo
     */
    public Grupoactivos getGrupo() {
        return grupo;
    }

    /**
     * @param grupo the grupo to set
     */
    public void setGrupo(Grupoactivos grupo) {
        this.grupo = grupo;
    }

    /**
     * @return the esExterno
     */
    public boolean isEsExterno() {
        return esExterno;
    }

    /**
     * @param esExterno the esExterno to set
     */
    public void setEsExterno(boolean esExterno) {
        this.esExterno = esExterno;
    }

    /**
     * @return the externoEntidad
     */
    public Externos getExternoEntidad() {
        return externoEntidad;
    }

    /**
     * @param externoEntidad the externoEntidad to set
     */
    public void setExternoEntidad(Externos externoEntidad) {
        this.externoEntidad = externoEntidad;
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

//    public void cambiaApellido(ValueChangeEvent event) {
//        setEntidad(null);
//        if (event.getComponent() instanceof SelectInputText) {
//            // get the number of displayable records from the component
//            SelectInputText autoComplete
//                    = (SelectInputText) event.getComponent();
//            // get the new value typed by component user.
//            String newWord = (String) event.getNewValue();
//            //        getEmpresasBeans().setComercial("");
//
//            List<Entidades> aux = new LinkedList<>();
//            // traer la consulta
//            Map parametros = new HashMap();
//            String where = " o.activo=true";
//            where += " and  upper(o.apellidos) like :apellidos";
//            parametros.put("apellidos", newWord.toUpperCase() + "%");
//            parametros.put(";where", where);
//            parametros.put(";orden", " o.apellidos");
//            int total = ((SelectInputText) event.getComponent()).getRows();
//            // Contadores
//            parametros.put(";inicial", 0);
//            parametros.put(";final", total);
//            try {
//                aux = ejbEntidades.encontarParametros(parametros);
//            } catch (ConsultarException ex) {
//                MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
//                Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
//            }
//            setListaUsuarios(new ArrayList());
//            for (Entidades e : aux) {
//                SelectItem s = new SelectItem(e, e.getApellidos());
//                getListaUsuarios().add(s);
//            }
//            if (autoComplete.getSelectedItem() != null) {
//                setEntidad((Entidades) autoComplete.getSelectedItem().getValue());
//            } else {
//
//                Entidades tmp = null;
//                for (int i = 0, max = getListaUsuarios().size(); i < max; i++) {
//                    SelectItem e = (SelectItem) getListaUsuarios().get(i);
//                    if (e.getLabel().compareToIgnoreCase(newWord) == 0) {
//                        tmp = (Entidades) e.getValue();
//                    }
//
//                }
//                if (tmp != null) {
//                    setEntidad(tmp);
//                }
//            }
//
//        }
//    }
    /**
     * @return the entidad
     */
    public Entidades getEntidad() {
        return entidad;
    }

    /**
     * @param entidad the entidad to set
     */
    public void setEntidad(Entidades entidad) {
        this.entidad = entidad;
    }

    /**
     * @return the apellidos
     */
    public String getApellidos() {
        return apellidos;
    }

    /**
     * @param apellidos the apellidos to set
     */
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    /**
     * @return the listaUsuarios
     */
    public List getListaUsuarios() {
        return listaUsuarios;
    }

    /**
     * @param listaUsuarios the listaUsuarios to set
     */
    public void setListaUsuarios(List listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }

    /**
     * @return the poliza
     */
    public Polizas getPoliza() {
        return poliza;
    }

    /**
     * @param poliza the poliza to set
     */
    public void setPoliza(Polizas poliza) {
        this.poliza = poliza;
    }

    /**
     * @return the garantia
     */
    public int getGarantia() {
        return garantia;
    }

    /**
     * @param garantia the garantia to set
     */
    public void setGarantia(int garantia) {
        this.garantia = garantia;
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
     * @return the subgrupoBean
     */
    public SubGruposBean getSubgrupoBean() {
        return subgrupoBean;
    }

    /**
     * @param subgrupoBean the subgrupoBean to set
     */
    public void setSubgrupoBean(SubGruposBean subgrupoBean) {
        this.subgrupoBean = subgrupoBean;
    }

    /**
     * @return the operativo
     */
    public int getOperativo() {
        return operativo;
    }

    /**
     * @param operativo the operativo to set
     */
    public void setOperativo(int operativo) {
        this.operativo = operativo;
    }

    /**
     * @return the control
     */
    public int getControl() {
        return control;
    }

    /**
     * @param control the control to set
     */
    public void setControl(int control) {
        this.control = control;
    }

    /**
     * @return the valorAnterior
     */
    public double getValorAnterior() {
        return valorAnterior;
    }

    /**
     * @param valorAnterior the valorAnterior to set
     */
    public void setValorAnterior(double valorAnterior) {
        this.valorAnterior = valorAnterior;
    }

    /**
     * @return the listaDetalleToma
     */
    public List<Detalletoma> getListaDetalleToma() {
        return listaDetalleToma;
    }

    /**
     * @param listaDetalleToma the listaDetalleToma to set
     */
    public void setListaDetalleToma(List<Detalletoma> listaDetalleToma) {
        this.listaDetalleToma = listaDetalleToma;
    }

    /**
     * @return the toma
     */
    public Tomafisica getToma() {
        return toma;
    }

    /**
     * @param toma the toma to set
     */
    public void setToma(Tomafisica toma) {
        this.toma = toma;
    }

    /**
     * @return the detalleToma
     */
    public Detalletoma getDetalleToma() {
        return detalleToma;
    }

    /**
     * @param detalleToma the detalleToma to set
     */
    public void setDetalleToma(Detalletoma detalleToma) {
        this.detalleToma = detalleToma;
    }

    /**
     * @return the formularioToma
     */
    public Formulario getFormularioToma() {
        return formularioToma;
    }

    /**
     * @param formularioToma the formularioToma to set
     */
    public void setFormularioToma(Formulario formularioToma) {
        this.formularioToma = formularioToma;
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
     * @return the listaTomas
     */
    public LazyDataModel<Tomafisica> getListaTomas() {
        return listaTomas;
    }

    /**
     * @param listaTomas the listaTomas to set
     */
    public void setListaTomas(LazyDataModel<Tomafisica> listaTomas) {
        this.listaTomas = listaTomas;
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

    public String salir() {
        listaDetalleToma = new LinkedList<>();
        listaDiferencias = new LinkedList<>();
        listaNuevos = new LinkedList<>();
        listaVerificados = new LinkedList<>();
        formulario.cancelar();
        formularioToma.cancelar();
        return null;
    }

    public void archivoListener(FileEntryEvent e) {
        FileEntry fe = (FileEntry) e.getComponent();
        FileEntryResults results = fe.getResults();
        File parent = null;

        //get data About File
        for (FileEntryResults.FileInfo i : results.getFiles()) {

            if (i.isSaved()) {

                File file = i.getFile();
                if (file != null) {
                    parent = file.getParentFile();

                    BufferedReader entrada = null;
                    try {

                        entrada = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
                        //                        entrada = new BufferedReader(new FileReader(file));

                        String sb;
//                        try {
                        // linea de cabeceras
                        sb = entrada.readLine();
                        String[] cabecera = sb.split(getSeparador());// lee los caracteres en el arreglo

                        int registro = 0;
                        while ((sb = entrada.readLine()) != null) {

                            String[] aux = sb.split(getSeparador());// lee los caracteres en el arreglo
                            Detalletoma d = new Detalletoma();
                            for (int j = 0; j < aux.length; j++) {

                                if (j < cabecera.length) {
                                    ubicar(d, cabecera[j].toLowerCase(), aux[j].toUpperCase());
                                }
                            }
                            if ((d.getCodigobarras() == null) || (d.getCodigobarras().isEmpty())) {
                                MensajesErrores.fatal("No existe inf de código de barras");
                                return;
                            }
                            Map parametros = new HashMap();
                            parametros.put(";where", "o.codigobarras=:codigo");
                            parametros.put("codigo", d.getCodigobarras());
                            List<Detalletoma> dl = ejbDetalleToma.encontarParametros(parametros);
                            if (dl.isEmpty()) {
                                // es nuevo
                                // colocar en lista como nuevo
                                agregarNuevo(d);
                                ejbDetalleToma.create(d, seguridadbean.getLogueado().getUserid());
                            } else {
                                // ya existen en la bd pero pueden ser nuevos o con errores
                                // es uno el que viene el lazo es solo por si acaso
                                for (Detalletoma dt : dl) {
                                    if (dt.getActivo() == null) {
                                        // es nuevo
                                        d.setObservaciones(dt.getObservaciones());
                                        d.setEstadoStr(dt.getEstadoStr());
                                        d.setCustodioStr(dt.getCustodioStr());
                                        d.setLocalidadStr(dt.getLocalidadStr());
                                        ejbDetalleToma.create(d, seguridadbean.getLogueado().getUserid());
                                        agregarNuevo(d);
                                    } else {
                                        // ver que pasa y que no este en otras listas incluso en las de nuevo
                                        sacarLista(dt, listaNuevos);
                                        sacarLista(dt, listaDiferencias);
                                        sacarLista(dt, listaVerificados);
                                        boolean cambia = false;
                                        // aqui ver que pasa
                                        // ver el más fácil empleado
                                        if (!((d.getCustodioStr() == null) || (d.getCustodioStr().isEmpty()))) {
                                            parametros = new HashMap();
                                            parametros.put(";where", "o.pin=:pin");
                                            parametros.put("pin", d.getCustodioStr());
                                            Entidades custodio = null;
                                            List<Entidades> le = ejbEntidades.encontarParametros(parametros);
                                            for (Entidades ent : le) {
                                                custodio = ent;
                                            }

                                            if (custodio != null) {
                                                if (dt.getCustodio() == null) {
                                                    cambia = true;
                                                    dt.setNuevocustodio(custodio.getId());
                                                    dt.setEscustodio(true);
                                                } else if (!dt.getCustodio().equals(custodio)) {
                                                    //cambio custodio
                                                    cambia = true;
                                                    dt.setNuevocustodio(custodio.getId());
                                                    dt.setEscustodio(true);
                                                }
                                            }
                                        }
                                        // ver localidad
                                        if (!((d.getLocalidadStr() == null) || (d.getLocalidadStr().isEmpty()))) {
                                            parametros = new HashMap();
                                            parametros.put(";where", "o.nombre=:nombre");
                                            parametros.put("nombre", d.getLocalidadStr());
                                            Oficinas localidad = null;
                                            List<Oficinas> lo = ejbOficina.encontarParametros(parametros);
                                            for (Oficinas o : lo) {
                                                localidad = o;
                                            }
                                            if (localidad != null) {
                                                if (dt.getLocalidad() == null) {
                                                    cambia = true;
                                                    dt.setNuevalocalidad(localidad.getId());
                                                    dt.setEslocalidad(true);
                                                } else if (!dt.getLocalidad().equals(localidad)) {
                                                    //cambio custodio
                                                    cambia = true;
                                                    dt.setNuevalocalidad(localidad.getId());
                                                    dt.setEslocalidad(true);
                                                }
                                            }
                                        }
                                        // ver estado
                                        if (!((d.getEstadoStr() == null) || (d.getEstadoStr().isEmpty()))) {
                                            parametros = new HashMap();
                                            parametros.put(";where", "o.nombre=:nombre");
                                            parametros.put("nombre", d.getEstadoStr());
                                            Codigos estadoLocal = null;
                                            List<Codigos> lc = ejbCodigos.encontarParametros(parametros);
                                            for (Codigos c : lc) {
                                                estadoLocal = c;
                                            }
                                            if (estadoLocal != null) {
                                                if (dt.getEsestado() == null) {
                                                    cambia = true;
                                                    dt.setNuevoestado(estadoLocal.getId());
                                                    dt.setEsestado(true);

                                                } else if (!dt.getEstado().equals(estadoLocal)) {
                                                    //cambio custodio
                                                    cambia = true;
                                                    dt.setNuevoestado(estadoLocal.getId());
                                                    dt.setEsestado(true);
                                                }
                                            }
                                        }
                                        dt.setVerificado(true);
                                        ejbDetalleToma.edit(dt, seguridadbean.getLogueado().getUserid());
                                        // ver si hubo cambios toca mandar a lista correcta
                                        if (cambia) {
                                            listaDiferencias.add(dt);
                                        } else {
                                            listaVerificados.add(dt);
                                        }
                                    }// fin else
                                } // fin for
                            }
                            registro++;

                        }

                        entrada.close();
                    } catch (UnsupportedEncodingException | FileNotFoundException | ConsultarException | InsertarException | GrabarException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger.getLogger(CargarAsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);

                    } catch (IOException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger.getLogger(CargarAsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
//               

                }
            } else {
                MensajesErrores.fatal("Archivo no puede ser cargado: "
                        + i.getStatus().getFacesMessage(
                                FacesContext.getCurrentInstance(),
                                fe, i).getSummary());
            }
        }

    }

    private boolean agregarNuevo(Detalletoma dt) {
        for (Detalletoma d : listaNuevos) {
            if (d.getCodigobarras().equals(dt.getCodigobarras())) {
                if (dt.getId() != null) {
                    d = dt;
                } else {
                    d.setObservaciones(dt.getObservaciones());
                    d.setEstadoStr(dt.getEstadoStr());
                    d.setCustodioStr(dt.getCustodioStr());
                    d.setLocalidadStr(dt.getLocalidadStr());
                }
                return false;
            }
        }
        listaNuevos.add(dt);
        return true;
    }

    private void sacarLista(Detalletoma dt, List<Detalletoma> dtl) {
        int i = 0;
        for (Detalletoma d : dtl) {
            if (d.getCodigobarras().equals(dt.getCodigobarras())) {
                dtl.remove(i);
                return;
            }
            i++;
        }
    }

    private void ubicar(Detalletoma am, String titulo, String valor) {

        if (titulo.contains("codigo")) {
            am.setCodigobarras(valor);

        } else if (titulo.contains("estado")) {
            // buscar el clasificador
            am.setEstadoStr(valor);
        } else if (titulo.contains("custodio")) {
            // buscar el clasificador
            am.setCustodioStr(valor);
        } else if (titulo.contains("observaciones")) {
            am.setObservaciones(valor);
        } else if (titulo.contains("ubicacion")) {
            am.setLocalidadStr(valor);
        } else if (titulo.contains("oficina")) {
            am.setLocalidadStr(valor);
        }

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
     * @return the listaNuevos
     */
    public List<Detalletoma> getListaNuevos() {
        return listaNuevos;
    }

    /**
     * @param listaNuevos the listaNuevos to set
     */
    public void setListaNuevos(List<Detalletoma> listaNuevos) {
        this.listaNuevos = listaNuevos;
    }

    /**
     * @return the listaVerificados
     */
    public List<Detalletoma> getListaVerificados() {
        return listaVerificados;
    }

    /**
     * @param listaVerificados the listaVerificados to set
     */
    public void setListaVerificados(List<Detalletoma> listaVerificados) {
        this.listaVerificados = listaVerificados;
    }

    /**
     * @return the listaDiferencias
     */
    public List<Detalletoma> getListaDiferencias() {
        return listaDiferencias;
    }

    /**
     * @param listaDiferencias the listaDiferencias to set
     */
    public void setListaDiferencias(List<Detalletoma> listaDiferencias) {
        this.listaDiferencias = listaDiferencias;
    }

    public String nuevoDetalle() {
        detalleToma = new Detalletoma();
        oficinaNueva = null;
        estadoNuevo = null;
        entidad = null;
        apellidos = null;
        formulario.insertar();
        return null;
    }

    public String modificaDetalle() {
        detalleToma = listaDetalleToma.get(formulario.getFila().getRowIndex());
        oficinaNueva = detalleToma.getLocalidad();
        estadoNuevo = detalleToma.getEstado();
        oficinaNueva = detalleToma.getLocalidad();
        if (detalleToma.getCustodio() != null) {
            entidad = detalleToma.getCustodio();
            apellidos = detalleToma.getCustodio().getApellidos();
        }
        formulario.editar();
        return null;
    }

    public String grabaNuevo() {
        if ((detalleToma.getCodigobarras() == null) || (detalleToma.getCodigobarras().isEmpty())) {
            MensajesErrores.advertencia("Necesario código de activo");
            return null;
        }
        if ((detalleToma.getObservaciones() == null) || (detalleToma.getObservaciones().isEmpty())) {
            MensajesErrores.advertencia("Necesario observaciones de activo");
            return null;
        }
        try {
            if (oficinaNueva != null) {
                detalleToma.setNuevalocalidad(oficinaNueva.getId());
            }
            if (estadoNuevo != null) {
                detalleToma.setNuevoestado(estadoNuevo.getId());
            }
            if (entidad != null) {
                detalleToma.setNuevocustodio(entidad.getId());
            }
            if (agregarNuevo(detalleToma)) {
                ejbDetalleToma.create(detalleToma, getSeguridadbean().getLogueado().getUserid());
            }
        } catch (InsertarException ex) {
            MensajesErrores.advertencia(ex.getMessage());
            Logger.getLogger(TomaFisicaBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formulario.cancelar();
        return null;
    }

    public String grabarDetalle() {
        // ver lo que cambia del original
        boolean cambia = false;
        if (oficinaNueva != null) {
            if (!detalleToma.getLocalidad().equals(oficinaNueva)) {
                // cambia la localidad
                detalleToma.setLocalidadStr(oficinaNueva.getNombre());
                detalleToma.setNuevalocalidad(oficinaNueva.getId());
                detalleToma.setEslocalidad(true);
                detalleToma.setVerificado(true);
                cambia = true;

            }
        }
        if (estadoNuevo != null) {
            if (!detalleToma.getEstado().equals(estadoNuevo)) {
                // cambia la localidad
                detalleToma.setEstadoStr(estadoNuevo.getNombre());
                detalleToma.setNuevoestado(estadoNuevo.getId());
                detalleToma.setEsestado(true);
                detalleToma.setVerificado(true);
                cambia = true;
            }
        }
        if (entidad != null) {
            if (!detalleToma.getCustodio().equals(entidad)) {
                // cambia la localidad
                detalleToma.setCustodioStr(entidad.getPin());
                detalleToma.setNuevocustodio(entidad.getId());
                detalleToma.setEscustodio(true);
                detalleToma.setVerificado(true);
                cambia = true;
            }
        }
        if (cambia) {
            try {
                sacarLista(detalleToma, listaVerificados);
                sacarLista(detalleToma, listaDiferencias);
                ejbDetalleToma.edit(detalleToma, getSeguridadbean().getLogueado().getUserid());
                listaDiferencias.add(detalleToma);
            } catch (GrabarException ex) {
                MensajesErrores.advertencia(ex.getMessage());
                Logger.getLogger(TomaFisicaBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        formulario.cancelar();
        return null;
    }

    public String eliminaNuevo(Detalletoma d) {
        sacarLista(d, listaNuevos);
        if (d.getId() != null) {
            try {
                ejbDetalleToma.remove(d, getSeguridadbean().getLogueado().getUserid());
            } catch (BorrarException ex) {
                MensajesErrores.advertencia(ex.getMessage());
                Logger.getLogger(TomaFisicaBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public String eliminaDiferencia(Detalletoma d) {
        sacarLista(d, listaDiferencias);
        if (d.getId() != null) {
            try {
                d.setVerificado(false);
                d.setNuevalocalidad(null);
                d.setNuevocustodio(null);
                d.setNuevoestado(null);
                d.setEscustodio(false);
                d.setEslocalidad(false);
                d.setEsestado(false);
                ejbDetalleToma.edit(d, getSeguridadbean().getLogueado().getUserid());
            } catch (GrabarException ex) {
                MensajesErrores.advertencia(ex.getMessage());
                Logger.getLogger(TomaFisicaBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public String eliminaVerificado(Detalletoma d) {
        sacarLista(d, listaDiferencias);
        if (d.getId() != null) {
            try {
                d.setVerificado(false);
                d.setNuevalocalidad(null);
                d.setNuevocustodio(null);
                d.setNuevoestado(null);
                d.setEscustodio(false);
                d.setEslocalidad(false);
                d.setEsestado(false);
                ejbDetalleToma.edit(d, getSeguridadbean().getLogueado().getUserid());
            } catch (GrabarException ex) {
                MensajesErrores.advertencia(ex.getMessage());
                Logger.getLogger(TomaFisicaBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    /**
     * @return the estadoNuevo
     */
    public Codigos getEstadoNuevo() {
        return estadoNuevo;
    }

    /**
     * @param estadoNuevo the estadoNuevo to set
     */
    public void setEstadoNuevo(Codigos estadoNuevo) {
        this.estadoNuevo = estadoNuevo;
    }

    /**
     * @return the oficinaNueva
     */
    public Oficinas getOficinaNueva() {
        return oficinaNueva;
    }

    /**
     * @param oficinaNueva the oficinaNueva to set
     */
    public void setOficinaNueva(Oficinas oficinaNueva) {
        this.oficinaNueva = oficinaNueva;
    }

    public String cancelar() {
        formularioToma.cancelar();
        return "ActivosVista.jsf?faces-redirect=true";
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
     * @return the marca
     */
    public Marcas getMarca() {
        return marca;
    }

    /**
     * @param marca the marca to set
     */
    public void setMarca(Marcas marca) {
        this.marca = marca;
    }

}
