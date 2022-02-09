/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.activos.sfccbdmq;

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
import org.beans.sfccbdmq.TrackingactivosFacade;
import org.entidades.sfccbdmq.Activoobligacion;
import org.entidades.sfccbdmq.Activos;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Edificios;
import org.entidades.sfccbdmq.Entidades;
import org.entidades.sfccbdmq.Externos;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Grupoactivos;
import org.entidades.sfccbdmq.Oficinas;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Polizas;
import org.entidades.sfccbdmq.Tipoactivo;
import org.entidades.sfccbdmq.Tipomovactivos;
import org.entidades.sfccbdmq.Trackingactivos;
import org.errores.sfccbdmq.ConsultarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "reporteTrackingActivosSfccbdmq")
@ViewScoped
public class ReporteTrackingActivosBean {

    /**
     * Creates a new instance of ActivoBean
     */
    public ReporteTrackingActivosBean() {
        activos = new LazyDataModel<Trackingactivos>() {

            @Override
            public List<Trackingactivos> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }

    @EJB
    private TrackingactivosFacade ejbTracking;
//    private int tamano;
    private String codigoAnterior;
    private Formatos nivel;
    private Formulario formulario = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private LazyDataModel<Trackingactivos> activos;
    private List<Activoobligacion> obligaciones;
    private List<Activos> hijos;
    private String codigo;
    private String nombre;
    private String nombreExterno;
    private String cedulaExterno;
    private String descripcion;
    private String inventario;
    private String alterno;
    private String marca;
    private String modelo;
    private String numeroserie;
    private String codigobarras;
    private String externo;
    private Tipoactivo tipo;
    private Integer factura;
    private Grupoactivos grupo;
    private Codigos clasificacion;
    private Codigos estado;
    private Codigos institucion;
    private double valorAnterior;
    private Oficinas oficina;
    private Polizas poliza;
    private Edificios edificio;
    private Externos externoEntidad = new Externos();
    private Entidades entidad;
    private String apellidos;
    private String centroCosto;
    private List listaUsuarios;
    private boolean esExterno = false;
    private Tipomovactivos baja;
    private Tipomovactivos alta;
    private int garantia;
    private int operativo;
    private int control;
    private Integer tipoTracking;
    private Integer numeroActa;
    private Integer numeroFactura;
    private Date desde;
    private Date hasta;
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
        hasta = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPinicial());
        desde = c.getTime();
        c.setTime(new Date());
        c.set(Calendar.DATE, 31);
        c.set(Calendar.MONTH, 11);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        hasta = c.getTime();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ReporteTrackingActivosVista";
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
    public LazyDataModel<Trackingactivos> getActivos() {
        return activos;
    }

    /**
     * @param activos the activos to set
     */
    public void setActivos(LazyDataModel<Trackingactivos> activos) {
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

    public String buscar() {
        activos = new LazyDataModel<Trackingactivos>() {

            @Override
            public List<Trackingactivos> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                if (scs.length == 0) {
                    parametros.put(";orden", "o.activo.id,o.fecha desc");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }
                String where = "  o.fecha  between :desde and :hasta ";
                parametros.put("desde", desde);
                parametros.put("hasta", hasta);
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }
                if (!((codigo == null) || (codigo.isEmpty()))) {
                    where += " and o.activo.codigo like :codigo";
                    parametros.put("codigo", codigo + "%");
                }
                if (!((centroCosto == null) || (centroCosto.isEmpty()))) {
                    where += " and o.activo.ccosto like :ccosto";
                    parametros.put("ccosto", getCentroCosto() + "%");
                }
                if (!((nombre == null) || (nombre.isEmpty()))) {
                    where += " and upper(o.activo.nombre) like :nombre";
                    parametros.put("nombre", nombre.toUpperCase() + "%");
                }
                if (!((nombreExterno == null) || (nombreExterno.isEmpty()))) {
                    where += " and upper(o.activo.externo.nombre) like :nombreExterno";
                    parametros.put("nombreExterno", nombreExterno.toUpperCase() + "%");
                }
                if (!((cedulaExterno == null) || (cedulaExterno.isEmpty()))) {
                    where += " and upper(o.activo.externo.empresa) like :cedulaExterno";
                    parametros.put("cedulaExterno", cedulaExterno.toUpperCase() + "%");
                }
                if (!((marca == null) || (marca.isEmpty()))) {
                    where += " and upper(o.activo.marca) like :marca";
                    parametros.put("marca", marca.toUpperCase() + "%");
                }
                if (!((descripcion == null) || (descripcion.isEmpty()))) {
                    where += " and upper(o.activo.descripcion) like :descripcion";
                    parametros.put("descripcion", descripcion.toUpperCase() + "%");
                }
                if (!((inventario == null) || (inventario.isEmpty()))) {
                    where += " and upper(o.activo.inventario) like :inventario";
                    parametros.put("inventario", inventario.toUpperCase() + "%");
                }
                if (!((alterno == null) || (alterno.isEmpty()))) {
                    where += " and upper(o.activo.alterno) like :alterno";
                    parametros.put("alterno", alterno.toUpperCase() + "%");
                }
                if (!((codigobarras == null) || (codigobarras.isEmpty()))) {
                    where += " and upper(o.activo.barras) like :codigobarras";
                    parametros.put("codigobarras", codigobarras.toUpperCase() + "%");
                }
                if (!((modelo == null) || (modelo.isEmpty()))) {
                    where += " and upper(o.activo.modelo) like :modelo";
                    parametros.put("modelo", modelo.toUpperCase() + "%");
                }
                if (!((numeroserie == null) || (numeroserie.isEmpty()))) {
                    where += " and upper(o.activo.numeroserie) like :numeroserie";
                    parametros.put("numeroserie", numeroserie.toUpperCase() + "%");
                }
                if (subgrupoBean.getGrupo() != null) {
                    if (subgrupoBean.getSubGrupo() != null) {
                        where += " and o.activo.subgrupo=:subgrupo";
                        parametros.put("subgrupo", subgrupoBean.getSubGrupo());
                    } else {
                        where += " and o.activo.grupo=:grupo";
                        parametros.put("grupo", subgrupoBean.getGrupo());
                    }
                }
                if (tipo != null) {
                    where += " and o.activo.grupo.tipo=:tipo";
                    parametros.put("tipo", tipo);
                }
                if (numeroActa != null) {
                    where += " and o.activo.actaultima=:actaultima";
                    parametros.put("actaultima", numeroActa);
                }
                if (numeroFactura != null) {
                    where += " and o.activo.factura=:factura";
                    parametros.put("factura", numeroFactura);
                }
                if (baja != null) {
                    where += " and o.activo.baja=:baja";
                    parametros.put("baja", baja);
                }
                if (alta != null) {
                    where += " and o.activo.alta=:alta";
                    parametros.put("alta", alta);
                }
                if (tipoTracking != null) {
                    if (tipoTracking != -100) {
                        where += " and o.tipo=:tipoTracking";
                        parametros.put("tipoTracking", tipoTracking);
                    }
                }
                if (estado != null) {
                    where += " and o.activo.estado=:estado";
                    parametros.put("estado", estado);
                }
                if (institucion != null) {
                    where += " and o.activo.externo.institucion=:institucion";
                    parametros.put("institucion", institucion);
                }
                if (clasificacion != null) {
                    where += " and o.activo.clasificacion=:clasificacion";
                    parametros.put("clasificacion", clasificacion);
                }
                if (poliza != null) {
                    where += " and o.activo.poliza=:poliza";
                    parametros.put("poliza", poliza);
                }
                if (proveedoresBean.getEmpresa() != null) {
                    where += " and o.activo.proveedor=:proveedor";
                    parametros.put("proveedor", proveedoresBean.getEmpresa().getProveedores());
                }
                if (entidadesBean.getEntidad() != null) {
                    where += " and o.activo.custodio=:custodio";
                    parametros.put("custodio", entidadesBean.getEntidad().getEmpleados());
                }
                if (getOficinasBean().getEdificio() != null) {
                    if (oficina != null) {
                        where += " and o.activo.localizacion=:localizacion";
                        parametros.put("localizacion", oficina);
                    } else {
                        where += " and o.activo.localizacion.edificio=:edificio";
                        parametros.put("edificio", getOficinasBean().getEdificio());
                    }
                }
                if (garantia == 1) {
                    where += " and o.activo.garantia=true ";
                } else if (garantia == -1) {
                    where += " and o.activo.garantia=false ";
                }
                if (operativo == 1) {
                    where += " and o.activo.operativo=true ";
                } else if (operativo == -1) {
                    where += " and o.activo.operativo=false ";
                }
                if (control == 1) {
                    where += " and o.activo.control=true ";
                } else if (control == -1) {
                    where += " and o.activo.control=false ";
                }
                int total = 0;

                try {
                    parametros.put(";where", where);
                    total = ejbTracking.contar(parametros);
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
                activos.setRowCount(total);
                try {
                    return ejbTracking.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        return null;
    }

    public String imprimir() {

        Map parametros = new HashMap();
        parametros.put(";orden", "o.activo.id,o.fecha desc");
        String where = "  o.fecha  between :desde and :hasta ";
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);
        if (!((codigo == null) || (codigo.isEmpty()))) {
            where += " and o.activo.codigo like :codigo";
            parametros.put("codigo", codigo + "%");
        }
        if (!((centroCosto == null) || (centroCosto.isEmpty()))) {
            where += " and o.activo.ccosto like :ccosto";
            parametros.put("ccosto", getCentroCosto() + "%");
        }
        if (!((nombre == null) || (nombre.isEmpty()))) {
            where += " and upper(o.activo.nombre) like :nombre";
            parametros.put("nombre", nombre.toUpperCase() + "%");
        }
        if (!((nombreExterno == null) || (nombreExterno.isEmpty()))) {
            where += " and upper(o.activo.externo.nombre) like :nombreExterno";
            parametros.put("nombreExterno", nombreExterno.toUpperCase() + "%");
        }
        if (!((cedulaExterno == null) || (cedulaExterno.isEmpty()))) {
            where += " and upper(o.activo.externo.empresa) like :cedulaExterno";
            parametros.put("cedulaExterno", cedulaExterno.toUpperCase() + "%");
        }
        if (!((marca == null) || (marca.isEmpty()))) {
            where += " and upper(o.activo.marca) like :marca";
            parametros.put("marca", marca.toUpperCase() + "%");
        }
        if (!((descripcion == null) || (descripcion.isEmpty()))) {
            where += " and upper(o.activo.descripcion) like :descripcion";
            parametros.put("descripcion", descripcion.toUpperCase() + "%");
        }
        if (!((inventario == null) || (inventario.isEmpty()))) {
            where += " and upper(o.activo.inventario) like :inventario";
            parametros.put("inventario", inventario.toUpperCase() + "%");
        }
        if (!((alterno == null) || (alterno.isEmpty()))) {
            where += " and upper(o.activo.alterno) like :alterno";
            parametros.put("alterno", alterno.toUpperCase() + "%");
        }
        if (!((codigobarras == null) || (codigobarras.isEmpty()))) {
            where += " and upper(o.activo.barras) like :codigobarras";
            parametros.put("codigobarras", codigobarras.toUpperCase() + "%");
        }
        if (!((modelo == null) || (modelo.isEmpty()))) {
            where += " and upper(o.activo.modelo) like :modelo";
            parametros.put("modelo", modelo.toUpperCase() + "%");
        }
        if (!((numeroserie == null) || (numeroserie.isEmpty()))) {
            where += " and upper(o.activo.numeroserie) like :numeroserie";
            parametros.put("numeroserie", numeroserie.toUpperCase() + "%");
        }
        if (subgrupoBean.getGrupo() != null) {
            if (subgrupoBean.getSubGrupo() != null) {
                where += " and o.activo.subgrupo=:subgrupo";
                parametros.put("subgrupo", subgrupoBean.getSubGrupo());
            } else {
                where += " and o.activo.grupo=:grupo";
                parametros.put("grupo", subgrupoBean.getGrupo());
            }
        }
        if (tipo != null) {
            where += " and o.activo.grupo.tipo=:tipo";
            parametros.put("tipo", tipo);
        }
        if (numeroActa != null) {
            where += " and o.activo.actaultima=:actaultima";
            parametros.put("actaultima", numeroActa);
        }
        if (numeroFactura != null) {
            where += " and o.activo.factura=:factura";
            parametros.put("factura", numeroFactura);
        }
        if (baja != null) {
            where += " and o.activo.baja=:baja";
            parametros.put("baja", baja);
        }
        if (alta != null) {
            where += " and o.activo.alta=:alta";
            parametros.put("alta", alta);
        }
        if (tipoTracking != null) {
            if (tipoTracking != -100) {
                where += " and o.tipo=:tipoTracking";
                parametros.put("tipoTracking", tipoTracking);
            }
        }
        if (estado != null) {
            where += " and o.activo.estado=:estado";
            parametros.put("estado", estado);
        }
        if (institucion != null) {
            where += " and o.activo.externo.institucion=:institucion";
            parametros.put("institucion", institucion);
        }
        if (clasificacion != null) {
            where += " and o.activo.clasificacion=:clasificacion";
            parametros.put("clasificacion", clasificacion);
        }
        if (poliza != null) {
            where += " and o.activo.poliza=:poliza";
            parametros.put("poliza", poliza);
        }
        if (proveedoresBean.getEmpresa() != null) {
            where += " and o.activo.proveedor=:proveedor";
            parametros.put("proveedor", proveedoresBean.getEmpresa().getProveedores());
        }
        if (entidadesBean.getEntidad() != null) {
            where += " and o.activo.custodio=:custodio";
            parametros.put("custodio", entidadesBean.getEntidad().getEmpleados());
        }
        if (getOficinasBean().getEdificio() != null) {
            if (oficina != null) {
                where += " and o.activo.localizacion=:localizacion";
                parametros.put("localizacion", oficina);
            } else {
                where += " and o.activo.localizacion.edificio=:edificio";
                parametros.put("edificio", getOficinasBean().getEdificio());
            }
        }
        if (garantia == 1) {
            where += " and o.activo.garantia=true ";
        } else if (garantia == -1) {
            where += " and o.activo.garantia=false ";
        }
        if (operativo == 1) {
            where += " and o.activo.operativo=true ";
        } else if (operativo == -1) {
            where += " and o.activo.operativo=false ";
        }
        if (control == 1) {
            where += " and o.activo.control=true ";
        } else if (control == -1) {
            where += " and o.activo.control=false ";
        }
        try {
            parametros.put(";where", where);
            List<Trackingactivos> lista = ejbTracking.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
            parametros.put("expresado", "Detalle de Transacciones dde Bienes" );
            parametros.put("nombrelogo", "logo-new.png");
            parametros.put("usuario", seguridadbean.getLogueado().getUserid());
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            Calendar c = Calendar.getInstance();
            reporte = new Reportesds(parametros,
                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/EventosActivos.jasper"),
                    lista, "Eventos" + String.valueOf(c.getTimeInMillis()) + ".pdf");
            formularioImprimir.insertar();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
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
     * @return the marca
     */
    public String getMarca() {
        return marca;
    }

    /**
     * @param marca the marca to set
     */
    public void setMarca(String marca) {
        this.marca = marca;
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
     * @return the tipoTracking
     */
    public Integer getTipoTracking() {
        return tipoTracking;
    }

    /**
     * @param tipoTracking the tipoTracking to set
     */
    public void setTipoTracking(Integer tipoTracking) {
        this.tipoTracking = tipoTracking;
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
     * @return the numeroActa
     */
    public Integer getNumeroActa() {
        return numeroActa;
    }

    /**
     * @param numeroActa the numeroActa to set
     */
    public void setNumeroActa(Integer numeroActa) {
        this.numeroActa = numeroActa;
    }

    /**
     * @return the baja
     */
    public Tipomovactivos getBaja() {
        return baja;
    }

    /**
     * @param baja the baja to set
     */
    public void setBaja(Tipomovactivos baja) {
        this.baja = baja;
    }

    /**
     * @return the alta
     */
    public Tipomovactivos getAlta() {
        return alta;
    }

    /**
     * @param alta the alta to set
     */
    public void setAlta(Tipomovactivos alta) {
        this.alta = alta;
    }

    /**
     * @return the centroCosto
     */
    public String getCentroCosto() {
        return centroCosto;
    }

    /**
     * @param centroCosto the centroCosto to set
     */
    public void setCentroCosto(String centroCosto) {
        this.centroCosto = centroCosto;
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
}
