/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.activos.sfccbdmq;

import com.ejb.sfcarchivos.ArchivosFacade;
import com.entidades.sfcarchivos.Archivos;
import com.entidades.sfcarchivos.Imagenes;
import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import javax.faces.application.Resource;
import org.compras.sfccbdmq.ProveedoresBean;
import org.presupuestos.sfccbdmq.ClasificadorBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.Reportesds;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
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
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.beans.sfccbdmq.ActasactivosFacade;
import org.beans.sfccbdmq.ActivoobligacionFacade;
import org.beans.sfccbdmq.ActivopolizaFacade;
import org.beans.sfccbdmq.ActivosFacade;
import org.beans.sfccbdmq.DepreciacionesFacade;
import org.beans.sfccbdmq.EntidadesFacade;
import org.beans.sfccbdmq.ErogacionesFacade;
import org.beans.sfccbdmq.TrackingactivosFacade;
import org.entidades.sfccbdmq.Actasactivos;
import org.entidades.sfccbdmq.Activoobligacion;
import org.entidades.sfccbdmq.Activopoliza;
import org.entidades.sfccbdmq.Activos;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Depreciaciones;
import org.entidades.sfccbdmq.Edificios;
import org.entidades.sfccbdmq.Entidades;
import org.entidades.sfccbdmq.Erogaciones;
import org.entidades.sfccbdmq.Externos;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Grupoactivos;
import org.entidades.sfccbdmq.Marcas;
import org.entidades.sfccbdmq.Oficinas;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Polizas;
import org.entidades.sfccbdmq.Tipoactivo;
import org.entidades.sfccbdmq.Trackingactivos;
import org.errores.sfccbdmq.ConsultarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.talento.sfccbdmq.RolEmpleadoBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "reporteCustodiosAnterioresSfccbdmq")
@ViewScoped
public class ReporteCustodioAnterioresBean implements Serializable {

    /**
     * Creates a new instance of ActivoBean
     */
    public ReporteCustodioAnterioresBean() {
        activos = new LazyDataModel<Activos>() {

            @Override
            public List<Activos> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }
    private List<Erogaciones> listadoErogaciones;
    @EJB
    private ErogacionesFacade ejbErogaciones;
    @EJB
    private ActivosFacade ejbActivos;
    @EJB
    private DepreciacionesFacade ejbDepreciaciones;
    @EJB
    private TrackingactivosFacade ejbTracking;
    @EJB
    private ActivopolizaFacade ejbActPol;
    @EJB
    private ActivoobligacionFacade ejbObligacion;
    @EJB
    private ArchivosFacade ejbArchivo;
    @EJB
    private ActasactivosFacade ejbActasactivos;
    @EJB
    private EntidadesFacade ejbEntidades;
    private Archivos archivoImagen;
//    private int tamano;
    private Imagenes imagen;

    private String codigoAnterior;
    private Formatos nivel;
    private Activos activo;
    private Formulario formulario = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    // autocompletar paar que ponga el custodio
    private Entidades entidad;
    private String apellidos;
    private List listaUsuarios;
    private boolean esExterno = false;
    private Externos externoEntidad = new Externos();
    private Double valorErogaciones;
    //
    private LazyDataModel<Activos> activos;
    private List<Activoobligacion> obligaciones;
    private List<Activopoliza> polizas;
    private List<Activos> hijos;
    private List<Trackingactivos> tackings;
//    private Trackingactivos tacking;
    private List<Depreciaciones> depreciaciones;
    private String codigo;
    private String nombre;
    private String nombreExterno;
    private String cedulaExterno;
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
    private Codigos metododepreciacion;
    private Codigos estado;
    private Codigos institucion;
    private Oficinas oficina;
    private Edificios edificio;
    private Polizas poliza;
    private List listaActivo;
    private Double deprecicionAcumulada;
    private Integer factura;
    private int garantia;
    private int operativo;
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
    @ManagedProperty(value = "#{oficinasSfccbdmq}")
    private OficinasBean oficinasBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    @ManagedProperty(value = "#{subGruposActivosSfccbdmq}")
    private SubGruposBean subgrupoBean;
    private Perfiles perfil;
    private Resource reporte;
    private Resource reportexsl;
    private Formulario formularioReporte = new Formulario();
    private List<Activos> listaActivosExportar;

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "FichaActivosVista";
        if (perfil == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
        this.setPerfil(seguridadbean.traerPerfil(perfil));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
    }

    public String buscarImprimir() {
        Map parametros = new HashMap();
        parametros.put(";orden", "o.codigo");
        String where = "  o.baja is null";
        if (!((codigo == null) || (codigo.isEmpty()))) {
            where += " and o.codigo like :codigo";
            parametros.put("codigo", codigo + "%");
        }
        if (!((nombre == null) || (nombre.isEmpty()))) {
            where += " and upper(o.nombre) like :nombre";
            parametros.put("nombre", nombre.toUpperCase() + "%");
        }
        if (!((nombreExterno == null) || (nombreExterno.isEmpty()))) {
            where += " and upper(o.externo.nombre) like :nombreExterno";
            parametros.put("nombreExterno", nombreExterno.toUpperCase() + "%");
        }
        if (!((cedulaExterno == null) || (cedulaExterno.isEmpty()))) {
            where += " and upper(o.externo.empresa) like :cedulaExterno";
            parametros.put("cedulaExterno", cedulaExterno.toUpperCase() + "%");
        }
        if (!(marca == null)) {
            where += " and o.activomarca=:activomarca";
            parametros.put("activomarca", marca);
        }
        if (!((descripcion == null) || (descripcion.isEmpty()))) {
            where += " and upper(o.descripcion) like :descripcion";
            parametros.put("descripcion", descripcion.toUpperCase() + "%");
        }
        if (!((inventario == null) || (inventario.isEmpty()))) {
            where += " and upper(o.inventario) like :inventario";
            parametros.put("inventario", inventario.toUpperCase() + "%");
        }
        if (!((alterno == null) || (alterno.isEmpty()))) {
            where += " and upper(o.alterno) like :alterno";
            parametros.put("alterno", alterno.toUpperCase() + "%");
        }
        if (!((codigobarras == null) || (codigobarras.isEmpty()))) {
            where += " and upper(o.barras) like :codigobarras";
            parametros.put("codigobarras", codigobarras.toUpperCase() + "%");
        }
        if (!((modelo == null) || (modelo.isEmpty()))) {
            where += " and upper(o.modelo) like :modelo";
            parametros.put("modelo", modelo.toUpperCase() + "%");
        }
        if (!((numeroserie == null) || (numeroserie.isEmpty()))) {
            where += " and upper(o.numeroserie) like :numeroserie";
            parametros.put("numeroserie", numeroserie.toUpperCase() + "%");
        }

        if (getSubgrupoBean().getGrupo() != null) {
            if (getSubgrupoBean().getSubGrupo() != null) {
                where += " and o.subgrupo=:subgrupo";
                parametros.put("subgrupo", getSubgrupoBean().getSubGrupo());
            } else {
                where += " and o.grupo=:grupo";
                parametros.put("grupo", getSubgrupoBean().getGrupo());
            }
        }
        if (metododepreciacion != null) {
            where += " and o.grupo.metododepreciacion=:metododepreciacion";
            parametros.put("metododepreciacion", metododepreciacion);
        }
        if (tipo != null) {
            where += " and o.grupo.tipo=:tipo";
            parametros.put("tipo", tipo);
        }
        if (factura != null) {
            where += " and o.factura=:factura";
            parametros.put("factura", factura);
        }
        if (estado != null) {
            where += " and o.estado=:estado";
            parametros.put("estado", estado);
        }
        if (institucion != null) {
            where += " and o.externo.institucion=:institucion";
            parametros.put("institucion", institucion);
        }
        if (clasificacion != null) {
            where += " and o.clasificacion=:clasificacion";
            parametros.put("clasificacion", clasificacion);
        }
        if (poliza != null) {
            where += " and o.poliza=:poliza";
            parametros.put("poliza", poliza);
        }
        if (proveedoresBean.getEmpresa() != null) {
            where += " and o.proveedor=:proveedor";
            parametros.put("proveedor", proveedoresBean.getEmpresa().getProveedores());
        }
        if (entidadesBean.getEntidad() != null) {
            where += " and o.custodio=:custodio";
            parametros.put("custodio", entidadesBean.getEntidad().getEmpleados());
        }
        if (garantia == 1) {
            where += " and o.garantia=true ";
        } else if (garantia == -1) {
            where += " and o.garantia=false ";
        }
        if (operativo == 1) {
            where += " and o.operativo=true ";
        } else if (operativo == -1) {
            where += " and o.operativo=false ";
        }
        if (oficinasBean.getEdificio() != null) {
            if (oficina != null) {
                where += " and o.localizacion=:localizacion";
                parametros.put("localizacion", oficina);
            } else {
                where += " and o.localizacion.edificio=:edificio";
                parametros.put("edificio", oficinasBean.getEdificio());
            }
        }
        parametros.put(";where", where);
        try {
            List<Activos> listaActivos = ejbActivos.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
            parametros.put("expresado", "Detalle de Bienes ");
            parametros.put("nombrelogo", "logo-new.png");
            parametros.put("usuario", seguridadbean.getLogueado().getUserid());
            parametros.put("fecha", new Date());
            Calendar c = Calendar.getInstance();
            setReporte(new Reportesds(parametros,
                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/BienesTipo.jasper"),
                    listaActivos, "Bienes" + String.valueOf(c.getTimeInMillis()) + ".pdf"));
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        formularioImprimir.editar();
        return null;
    }

    public String buscar() {
        activos = new LazyDataModel<Activos>() {

            @Override
            public List<Activos> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                if (scs.length == 0) {
                    parametros.put(";orden", "o.codigo");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }
                String where = " o.id is not null";
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }
                if (!((codigo == null) || (codigo.isEmpty()))) {
                    where += " and o.codigo like :codigo";
                    parametros.put("codigo", "%" + codigo + "%");
                }
                if (!((nombre == null) || (nombre.isEmpty()))) {
                    where += " and upper(o.nombre) like :nombre";
                    parametros.put("nombre", nombre.toUpperCase() + "%");
                }
                if (!((nombreExterno == null) || (nombreExterno.isEmpty()))) {
                    where += " and upper(o.externo.nombre) like :nombreExterno";
                    parametros.put("nombreExterno", nombreExterno.toUpperCase() + "%");
                }
                if (!((cedulaExterno == null) || (cedulaExterno.isEmpty()))) {
                    where += " and upper(o.externo.empresa) like :cedulaExterno";
                    parametros.put("cedulaExterno", cedulaExterno.toUpperCase() + "%");
                }
//                if (!((marca == null) || (marca.isEmpty()))) {
//                    where += " and upper(o.marca) like :marca";
//                    parametros.put("marca", marca.toUpperCase() + "%");
//                }
                if (!(marca == null)) {
                    where += " and o.activomarca=:activomarca";
                    parametros.put("activomarca", marca);
                }
                if (!((descripcion == null) || (descripcion.isEmpty()))) {
                    where += " and upper(o.descripcion) like :descripcion";
                    parametros.put("descripcion", descripcion.toUpperCase() + "%");
                }
                if (!((inventario == null) || (inventario.isEmpty()))) {
                    where += " and upper(o.inventario) like :inventario";
                    parametros.put("inventario", inventario.toUpperCase() + "%");
                }
                if (!((alterno == null) || (alterno.isEmpty()))) {
                    where += " and upper(o.alterno) like :alterno";
                    parametros.put("alterno", alterno.toUpperCase() + "%");
                }
                if (!((codigobarras == null) || (codigobarras.isEmpty()))) {
                    where += " and upper(o.barras) like :codigobarras";
                    parametros.put("codigobarras", codigobarras.toUpperCase() + "%");
                }
                if (factura != null) {
                    where += " and o.factura=:factura";
                    parametros.put("factura", factura);
                }
                if (!((modelo == null) || (modelo.isEmpty()))) {
                    where += " and upper(o.modelo) like :modelo";
                    parametros.put("modelo", modelo.toUpperCase() + "%");
                }
                if (!((numeroserie == null) || (numeroserie.isEmpty()))) {
                    where += " and upper(o.numeroserie) like :numeroserie";
                    parametros.put("numeroserie", numeroserie.toUpperCase() + "%");
                }
//                if (grupo != null) {
//                    where += " and o.grupo=:grupo";
//                    parametros.put("grupo", grupo);
//                }
                if (getSubgrupoBean().getGrupo() != null) {
                    if (getSubgrupoBean().getSubGrupo() != null) {
                        where += " and o.subgrupo=:subgrupo";
                        parametros.put("subgrupo", getSubgrupoBean().getSubGrupo());
                    } else {
                        where += " and o.grupo=:grupo";
                        parametros.put("grupo", getSubgrupoBean().getGrupo());
                    }
                }
                if (metododepreciacion != null) {
                    where += " and o.grupo.metododepreciacion=:metododepreciacion";
                    parametros.put("metododepreciacion", metododepreciacion);
                }
                if (tipo != null) {
                    where += " and o.grupo.tipo=:tipo";
                    parametros.put("tipo", tipo);
                }
                if (estado != null) {
                    where += " and o.estado=:estado";
                    parametros.put("estado", estado);
                }
                if (institucion != null) {
                    where += " and o.externo.institucion=:institucion";
                    parametros.put("institucion", institucion);
                }
                if (clasificacion != null) {
                    where += " and o.clasificacion=:clasificacion";
                    parametros.put("clasificacion", clasificacion);
                }
                if (poliza != null) {
                    where += " and o.poliza=:poliza";
                    parametros.put("poliza", poliza);
                }
                if (proveedoresBean.getEmpresa() != null) {
                    where += " and o.proveedor=:proveedor";
                    parametros.put("proveedor", proveedoresBean.getEmpresa().getProveedores());
                }
                if (entidadesBean.getEntidad() != null) {
                    where += " and o.custodio=:custodio";
                    parametros.put("custodio", entidadesBean.getEntidad().getEmpleados());
                }
                if (garantia == 1) {
                    where += " and o.garantia=true ";
                } else if (garantia == -1) {
                    where += " and o.garantia=false ";
                }
                if (operativo == 1) {
                    where += " and o.operativo=true ";
                } else if (operativo == -1) {
                    where += " and o.operativo=false ";
                }
                if (oficinasBean.getEdificio() != null) {
                    if (oficina != null) {
                        where += " and o.localizacion=:localizacion";
                        parametros.put("localizacion", oficina);
                    } else {
                        where += " and o.localizacion.edificio=:edificio";
                        parametros.put("edificio", oficinasBean.getEdificio());
                    }
                }
                int total = 0;

                try {
                    parametros.put(";where", where);
                    listaActivosExportar = ejbActivos.encontarParametros(parametros);
                    total = ejbActivos.contar(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                if (pageSize > 0) {
                    int endIndex = i + pageSize;
                    if (endIndex > total) {
                        endIndex = total;
                    }

                    parametros.put(";inicial", i);
                    parametros.put(";final", endIndex);
                    activos.setRowCount(total);
                } else {
                    activos.setPageSize(1);
                }
                try {
                    return ejbActivos.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        return null;
    }

    public String usuarios(Activos act) {

        String general = "";
        Map parametros = new HashMap();
        String where = "o.padre=:padre and o.baja is null";
        parametros.put("padre", act);
        parametros.put(";where", where);
        try {
            setHijos(ejbActivos.encontarParametros(parametros));
            parametros = new HashMap();
            parametros.put(";orden", "o.id asc");
            parametros.put(";where", "o.activo=:padre and o.tipo not in (8,-8)");
            parametros.put("padre", act);
            tackings = ejbTracking.encontarParametros(parametros);
            for (Trackingactivos tra : tackings) {
                if (tra != null) {
                    general += traerEntidad(tra.getUsuario()) + " - ";
                } else {
                    general = "";
                }
                return general;
            }

        } catch (ConsultarException ex) {
            MensajesErrores.advertencia(ex.getMessage());
            Logger.getLogger(BajasActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String descripcion(Activos act) {

        String general = "";
        Map parametros = new HashMap();
        String where = "o.padre=:padre and o.baja is null";
        parametros.put("padre", act);
        parametros.put(";where", where);
        try {
            setHijos(ejbActivos.encontarParametros(parametros));
            parametros = new HashMap();
            parametros.put(";orden", "o.id asc");
            parametros.put(";where", "o.activo=:padre and o.tipo not in (8,-8)");
            parametros.put("padre", act);
            tackings = ejbTracking.encontarParametros(parametros);
            for (Trackingactivos tra : tackings) {
                if (tra != null) {

                    general += tra.getDescripcion() + " - ";
                } else {
                    general = "";
                }
                return general;
            }

        } catch (ConsultarException ex) {
            MensajesErrores.advertencia(ex.getMessage());
            Logger.getLogger(BajasActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String tipo(Activos act) {

        String general = "";
        Map parametros = new HashMap();
        String where = "o.padre=:padre and o.baja is null";
        parametros.put("padre", act);
        parametros.put(";where", where);
        try {
            setHijos(ejbActivos.encontarParametros(parametros));
            parametros = new HashMap();
            parametros.put(";orden", "o.id asc");
            parametros.put(";where", "o.activo=:padre and o.tipo not in (8,-8)");
            parametros.put("padre", act);
            tackings = ejbTracking.encontarParametros(parametros);
            for (Trackingactivos tra : tackings) {
                if (tra != null) {
                    general += getTipoStr(tra.getTipo()) + " - ";
                } else {
                    general = "";
                }
                return general;
            }

        } catch (ConsultarException ex) {
            MensajesErrores.advertencia(ex.getMessage());
            Logger.getLogger(BajasActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String fecha(Activos act) {

        String general = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy ");
        Map parametros = new HashMap();
        String where = "o.padre=:padre and o.baja is null";
        parametros.put("padre", act);
        parametros.put(";where", where);
        try {
            setHijos(ejbActivos.encontarParametros(parametros));
            parametros = new HashMap();
            parametros.put(";orden", "o.id asc");
            parametros.put(";where", "o.activo=:padre and o.tipo not in (8,-8)");
            parametros.put("padre", act);
            tackings = ejbTracking.encontarParametros(parametros);
            for (Trackingactivos tra : tackings) {
                if (tra != null) {
                    general += sdf.format(tra.getFecha()) + " - ";
                } else {
                    general = "";
                }
                return general;
            }

        } catch (ConsultarException ex) {
            MensajesErrores.advertencia(ex.getMessage());
            Logger.getLogger(BajasActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String cuentaAnterior(Activos act) {

        String general = "";
        Map parametros = new HashMap();
        String where = "o.padre=:padre and o.baja is null";
        parametros.put("padre", act);
        parametros.put(";where", where);
        try {
            setHijos(ejbActivos.encontarParametros(parametros));
            parametros = new HashMap();
            parametros.put(";orden", "o.id asc");
            parametros.put(";where", "o.activo=:padre and o.tipo not in (8,-8)");
            parametros.put("padre", act);
            tackings = ejbTracking.encontarParametros(parametros);
            for (Trackingactivos tra : tackings) {
                if (tra != null) {
                    general += tra.getCuenta1() + " - ";
                } else {
                    general = "";
                }
                return general;
            }

        } catch (ConsultarException ex) {
            MensajesErrores.advertencia(ex.getMessage());
            Logger.getLogger(BajasActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String cuentaActual(Activos act) {

        String general = "";
        Map parametros = new HashMap();
        String where = "o.padre=:padre and o.baja is null";
        parametros.put("padre", act);
        parametros.put(";where", where);
        try {
            setHijos(ejbActivos.encontarParametros(parametros));
            parametros = new HashMap();
            parametros.put(";orden", "o.id asc");
            parametros.put(";where", "o.activo=:padre and o.tipo not in (8,-8)");
            parametros.put("padre", act);
            tackings = ejbTracking.encontarParametros(parametros);
            for (Trackingactivos tra : tackings) {
                if (tra != null) {
                    general += tra.getCuenta2() + " - ";
                } else {
                    general = "";
                }
                return general;
            }

        } catch (ConsultarException ex) {
            MensajesErrores.advertencia(ex.getMessage());
            Logger.getLogger(BajasActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String valorAnterior(Activos act) {

        String general = "";
        Map parametros = new HashMap();
        String where = "o.padre=:padre and o.baja is null";
        parametros.put("padre", act);
        parametros.put(";where", where);
        try {
            setHijos(ejbActivos.encontarParametros(parametros));
            parametros = new HashMap();
            parametros.put(";orden", "o.id asc");
            parametros.put(";where", "o.activo=:padre and o.tipo not in (8,-8)");
            parametros.put("padre", act);
            tackings = ejbTracking.encontarParametros(parametros);
            for (Trackingactivos tra : tackings) {
                if (tra != null) {
                    if (tra.getValor() != null) {
                        general += tra.getValor().toString() + " - ";
                    }

                } else {
                    general = "";
                }
                return general;
            }

        } catch (ConsultarException ex) {
            MensajesErrores.advertencia(ex.getMessage());
            Logger.getLogger(BajasActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String valorNuevo(Activos act) {

        String general = "";
        Map parametros = new HashMap();
        String where = "o.padre=:padre and o.baja is null";
        parametros.put("padre", act);
        parametros.put(";where", where);
        try {
            setHijos(ejbActivos.encontarParametros(parametros));
            parametros = new HashMap();
            parametros.put(";orden", "o.id asc");
            parametros.put(";where", "o.activo=:padre and o.tipo not in (8,-8)");
            parametros.put("padre", act);
            tackings = ejbTracking.encontarParametros(parametros);
            for (Trackingactivos tra : tackings) {
                if (tra != null) {
                    if (tra.getValornuevo() != null) {
                        general += tra.getValornuevo().toString() + " - ";
                    }

                } else {
                    general = "";
                }
                return general;
            }

        } catch (ConsultarException ex) {
            MensajesErrores.advertencia(ex.getMessage());
            Logger.getLogger(BajasActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String acta(Activos act) {

        String general = "";
        Map parametros = new HashMap();
        String where = "o.padre=:padre and o.baja is null";
        parametros.put("padre", act);
        parametros.put(";where", where);
        try {
            setHijos(ejbActivos.encontarParametros(parametros));
            parametros = new HashMap();
            parametros.put(";orden", "o.id asc");
            parametros.put(";where", "o.activo=:padre and o.tipo not in (8,-8)");
            parametros.put("padre", act);
            tackings = ejbTracking.encontarParametros(parametros);
            for (Trackingactivos tra : tackings) {
                if (tra != null) {

                    general += tra.getActa() + " - ";

                } else {
                    general = "";
                }
                return general;
            }

        } catch (ConsultarException ex) {
            MensajesErrores.advertencia(ex.getMessage());
            Logger.getLogger(BajasActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String exportar() {
        try {
            if (activos == null) {
                MensajesErrores.advertencia("Genere el reporte primero");
                return null;
            }
            DocumentoXLS xls = new DocumentoXLS("Activos");
            List<AuxiliarReporte> campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "FECHA INGRESO"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "FECHA ALTA"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "GRUPO"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "GRUPO CONTABLE"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "CÓDIGO"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "CÓDIGO PADRE"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "NOMBRE"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "DESCRIPCIÓN"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "MARCA"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "MODELO"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "NO. SERIE"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "CÓDIGO ALTERNO"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "CÓDIGO DE BARRAS"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "ESTADO"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "VALOR ALTA"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "%VALOR RESIDUAL"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "CONTROL"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "INVVENTARIO"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "USUARIO CÉDULA"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "USUARIO NOMBRES"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "LOCALIZACIÓN"));

            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "ACTA ALTA"));
//            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "EJ/PER.CTBLE"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "ULTIMA ACTA CAMBIO"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "FECHA ULTIMO CAMBIO"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "NO. DE CHASIS"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "NO. DE PLACA"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "NO. DE MOTOR"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "NO. DE FAC"));
//            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "TIPO DOC"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "RUC PROVEEDOR"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "PROVEEDOR"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "OBSERVACION"));
            xls.agregarFila(campos, true);
            for (Activos e : listaActivosExportar) {
                if (e != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                    campos = new LinkedList<>();
                    campos.add(new AuxiliarReporte("String", e.getFechaingreso() != null ? (sdf.format(e.getFechaingreso())) : ""));
                    campos.add(new AuxiliarReporte("String", e.getFechaalta() != null ? (sdf.format(e.getFechaalta())) : ""));
                    campos.add(new AuxiliarReporte("String", e.getGrupo() != null ? e.getGrupo().getNombre() : ""));
                    campos.add(new AuxiliarReporte("String", e.getClasificacion() != null ? e.getClasificacion().getNombre() : ""));
                    campos.add(new AuxiliarReporte("String", e.getCodigo() != null ? e.getCodigo() : ""));
                    campos.add(new AuxiliarReporte("String", e.getPadre() != null ? e.getPadre().getCodigo() : ""));
                    campos.add(new AuxiliarReporte("String", e.getNombre() != null ? e.getNombre() : ""));
                    campos.add(new AuxiliarReporte("String", e.getDescripcion() != null ? e.getDescripcion() : ""));
                    campos.add(new AuxiliarReporte("String", e.getActivomarca() != null ? e.getActivomarca().getNombre() : ""));
                    campos.add(new AuxiliarReporte("String", e.getModelo() != null ? e.getModelo() : ""));
                    campos.add(new AuxiliarReporte("String", e.getNumeroserie() != null ? e.getNumeroserie() : ""));
                    campos.add(new AuxiliarReporte("String", e.getAlterno() != null ? e.getAlterno() : ""));
                    campos.add(new AuxiliarReporte("String", e.getBarras() != null ? e.getBarras() : ""));
                    campos.add(new AuxiliarReporte("String", e.getEstado() != null ? e.getEstado().getNombre() : ""));
                    campos.add(new AuxiliarReporte("String", e.getValoralta() != null ? (e.getValoralta().doubleValue() + "") : ""));
                    campos.add(new AuxiliarReporte("String", e.getValorresidual() != null ? (e.getValorresidual().doubleValue() + "") : ""));
                    campos.add(new AuxiliarReporte("String", e.getControl() != null ? (e.getControl() ? "SI" : "NO") : ""));
                    campos.add(new AuxiliarReporte("String", e.getInventario() != null ? e.getInventario() : ""));
                    campos.add(new AuxiliarReporte("String", e.getCustodio() != null ? e.getCustodio().getEntidad().getPin() : ""));
                    campos.add(new AuxiliarReporte("String", e.getCustodio() != null ? e.getCustodio().getEntidad().toString() : ""));
                    campos.add(new AuxiliarReporte("String", e.getLocalizacion() != null ? (e.getLocalizacion().getEdificio() + "-" + e.getLocalizacion().getNombre()) : ""));
                    int actaUlt;
                    if (e.getActaultima() == null) {
                        actaUlt = 0;
                    } else {
                        actaUlt = e.getActaultima();
                    }
                    String fechaUltima = traerFecha(e, actaUlt);
                    campos.add(new AuxiliarReporte("String", e.getActa() != null ? e.getActa() + "" : ""));
//                    campos.add(new AuxiliarReporte("String", "")); //"EJ/PER.CTBLE"
                    campos.add(new AuxiliarReporte("String", e.getActaultima() != null ? e.getActaultima() + "" : ""));
                    campos.add(new AuxiliarReporte("String", fechaUltima != null ? fechaUltima : ""));  //FECHA ULTIMO CAMBIO
                    campos.add(new AuxiliarReporte("String", e.getMarca() != null ? e.getMarca() : "")); //Chasis
                    campos.add(new AuxiliarReporte("String", e.getCaracteristicas() != null ? e.getCaracteristicas() : "")); //PLaca
                    campos.add(new AuxiliarReporte("String", e.getDimensiones() != null ? e.getDimensiones() : "")); //motor
                    campos.add(new AuxiliarReporte("String", e.getFactura() != null ? e.getFactura() : ""));
//                    campos.add(new AuxiliarReporte("String", "")); //TIPO DOC
                    campos.add(new AuxiliarReporte("String", e.getProveedor() != null ? e.getProveedor().getRucbeneficiario() : ""));
                    campos.add(new AuxiliarReporte("String", e.getProveedor() != null ? e.getProveedor().getEmpresa().getNombrecomercial() : ""));
                    campos.add(new AuxiliarReporte("String", e.getObservaciones() != null ? e.getObservaciones() : ""));
                    xls.agregarFila(campos, false);
                }
            }
            setReportexsl(xls.traerRecurso());
            getFormularioReporte().insertar();
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(RolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String traerFecha(Activos act, int numAct) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.activo=:activo and o.acta.numerotexto=:numerotexto");
        parametros.put("activo", act);
        parametros.put("numerotexto", numAct + "");
        try {
            Date fecha = null;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            List<Actasactivos> lista = ejbActasactivos.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                for (Actasactivos aa : lista) {
                    fecha = aa.getActa().getFecha();
                }
                if (fecha == null) {
                    String retorno = sdf.format(fecha) + "";
                    return retorno;
                } else {
                    return "";
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage());
            Logger.getLogger(ActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public String traerEntidad(String usuario) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.userid=:userid");
        parametros.put("userid", usuario);
        try {
            List<Entidades> lista = ejbEntidades.encontarParametros(parametros);
            Entidades e = null;
            if (!lista.isEmpty()) {
                e = lista.get(0);
                return e.toString();
            }
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage());
            Logger.getLogger(ActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String traerTipoEstado(Activos act) {
        if (act.getFechaingreso() != null) {
            if (act.getFechaingreso() != null && act.getCustodio() == null && act.getExterno() == null) {
                if (act.getFechaingreso() != null && act.getCustodio() == null && act.getExterno() == null && act.getBaja() != null && act.getFechasolicitud() != null) {
                    if (act.getFechaingreso() != null && act.getCustodio() == null && act.getExterno() == null && act.getBaja() != null && act.getFechasolicitud() != null && act.getFechabaja() != null) {
                        return "Dado de Baja";
                    }
                    return "Inicio de Baja";
                }
            } else {
                if (act.getFechaingreso() != null && (act.getCustodio() != null || act.getExterno() != null)) {
                    return "Asignado";
                }
            }
            return "Ingresado";
        }
        return "";
    }

    public List<Activos> getHijosFuera() {
        if (activos == null) {
            return null;
        }
        if (!activos.isRowAvailable()) {
            return null;
        }
        Activos a = (Activos) activos.getRowData();
        Map parametros = new HashMap();
        parametros.put(";where", "o.padre=:padre and o.baja is null");
        parametros.put("padre", a);
        try {
            return ejbActivos.encontarParametros(parametros);

        } catch (ConsultarException ex) {
            MensajesErrores.advertencia(ex.getMessage());
            Logger.getLogger(BajasActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public int getHijosNumero() {
        if (activos == null) {
            return 0;
        }
        if (!activos.isRowAvailable()) {
            return 0;
        }
        Activos a = (Activos) activos.getRowData();
        Map parametros = new HashMap();
        parametros.put(";where", "o.padre=:padre and o.baja is null");
        parametros.put("padre", a);
        try {
            return ejbActivos.contar(parametros);

        } catch (ConsultarException ex) {
            MensajesErrores.advertencia(ex.getMessage());
            Logger.getLogger(BajasActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double getDepreciacion() {
        try {
            Activos a = (Activos) activos.getRowData();
            Map parametros = new HashMap();
            parametros.put(";where", "o.activo=:activo");
            parametros.put(";campo", "o.valor");
            parametros.put("activo", a);
            return ejbDepreciaciones.sumarCampoDoble(parametros);

        } catch (ConsultarException ex) {
            Logger.getLogger(FichaActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public String getTipoStr(Integer tipoParametro) {
        if (tipoParametro == 0) {
            return "CAMBIA LOCALIDAD";
        } else if (tipoParametro == 1) {
            return "CAMBIA CUSTODIO";
        } else if (tipoParametro == 2) {
            return "ALTA";
        } else if (tipoParametro == 3) {
            return "CAMBIA VALOR";
        } else if (tipoParametro == 4) {
            return "CAMBIA  VIDA UTIL";
        } else if (tipoParametro == 5) {
            return "INCLUSION POLIZA";
        } else if (tipoParametro == 6) {
            return "EXCLUSION POLIZA";
        } else if (tipoParametro == 7) {
            return "RECLAMO SEGURO";
        } else if (tipoParametro == -1) {
            return "BAJA";
        } else if (tipoParametro == -2) {
            return "SOLICITUD BAJA";
        } else if (tipoParametro == -3) {
            return "REVERSA DEPRESICION";
        } else if (tipoParametro == 8) {
            return "DEPRECIACION";
        } else if (tipoParametro == 9) {
            return "CAMBIO ESTADO";
        }
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
     * @return the listaActivo
     */
    public List getListaActivo() {
        return listaActivo;
    }

    /**
     * @param listaActivo the listaActivo to set
     */
    public void setListaActivo(List listaActivo) {
        this.listaActivo = listaActivo;
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
     * @return the obligaciones
     */
    public List<Activoobligacion> getObligaciones() {
        return obligaciones;
    }

    /**
     * @param obligaciones the obligaciones to set
     */
    public void setObligaciones(List<Activoobligacion> obligaciones) {
        this.obligaciones = obligaciones;
    }

    /**
     * @return the hijos
     */
    public List<Activos> getHijos() {
        return hijos;
    }

    /**
     * @param hijos the hijos to set
     */
    public void setHijos(List<Activos> hijos) {
        this.hijos = hijos;
    }

    /**
     * @return the tackings
     */
    public List<Trackingactivos> getTackings() {
        return tackings;
    }

    /**
     * @param tackings the tackings to set
     */
    public void setTackings(List<Trackingactivos> tackings) {
        this.tackings = tackings;
    }

    /**
     * @return the depreciaciones
     */
    public List<Depreciaciones> getDepreciaciones() {
        return depreciaciones;
    }

    /**
     * @param depreciaciones the depreciaciones to set
     */
    public void setDepreciaciones(List<Depreciaciones> depreciaciones) {
        this.depreciaciones = depreciaciones;
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
     * @return the metododepreciacion
     */
    public Codigos getMetododepreciacion() {
        return metododepreciacion;
    }

    /**
     * @param metododepreciacion the metododepreciacion to set
     */
    public void setMetododepreciacion(Codigos metododepreciacion) {
        this.metododepreciacion = metododepreciacion;
    }

    /**
     * @return the listadoErogaciones
     */
    public List<Erogaciones> getListadoErogaciones() {
        return listadoErogaciones;
    }

    /**
     * @param listadoErogaciones the listadoErogaciones to set
     */
    public void setListadoErogaciones(List<Erogaciones> listadoErogaciones) {
        this.listadoErogaciones = listadoErogaciones;
    }

    /**
     * @return the institucion
     */
    public Codigos getInstitucion() {
        return institucion;
    }

    /**
     * @param institucion the institucion to set
     */
    public void setInstitucion(Codigos institucion) {
        this.institucion = institucion;
    }

    /**
     * @return the nombreExterno
     */
    public String getNombreExterno() {
        return nombreExterno;
    }

    /**
     * @param nombreExterno the nombreExterno to set
     */
    public void setNombreExterno(String nombreExterno) {
        this.nombreExterno = nombreExterno;
    }

    /**
     * @return the cedulaExterno
     */
    public String getCedulaExterno() {
        return cedulaExterno;
    }

    /**
     * @param cedulaExterno the cedulaExterno to set
     */
    public void setCedulaExterno(String cedulaExterno) {
        this.cedulaExterno = cedulaExterno;
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
     * @return the polizas
     */
    public List<Activopoliza> getPolizas() {
        return polizas;
    }

    /**
     * @param polizas the polizas to set
     */
    public void setPolizas(List<Activopoliza> polizas) {
        this.polizas = polizas;
    }

    /**
     * @return the deprecicionAcumulada
     */
    public Double getDeprecicionAcumulada() {
        return deprecicionAcumulada;
    }

    /**
     * @param deprecicionAcumulada the deprecicionAcumulada to set
     */
    public void setDeprecicionAcumulada(Double deprecicionAcumulada) {
        this.deprecicionAcumulada = deprecicionAcumulada;
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
     * @return the factura
     */
    public Integer getFactura() {
        return factura;
    }

    /**
     * @param factura the factura to set
     */
    public void setFactura(Integer factura) {
        this.factura = factura;
    }

    /**
     * @return the valorErogaciones
     */
    public Double getValorErogaciones() {
        return valorErogaciones;
    }

    /**
     * @param valorErogaciones the valorErogaciones to set
     */
    public void setValorErogaciones(Double valorErogaciones) {
        this.valorErogaciones = valorErogaciones;
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

    /**
     * @return the reportexsl
     */
    public Resource getReportexsl() {
        return reportexsl;
    }

    /**
     * @param reportexsl the reportexsl to set
     */
    public void setReportexsl(Resource reportexsl) {
        this.reportexsl = reportexsl;
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
     * @return the listaActivosExportar
     */
    public List<Activos> getListaActivosExportar() {
        return listaActivosExportar;
    }

    /**
     * @param listaActivosExportar the listaActivosExportar to set
     */
    public void setListaActivosExportar(List<Activos> listaActivosExportar) {
        this.listaActivosExportar = listaActivosExportar;
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
     * @return the activos
     */
    public LazyDataModel<Activos> getActivos() {
        return activos;
    }

    /**
     * @param activos the activos to set
     */
    public void setActivos(LazyDataModel<Activos> activos) {
        this.activos = activos;
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
}