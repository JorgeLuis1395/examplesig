/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.activos.sfccbdmq;

//import com.icesoft.faces.component.selectinputtext.SelectInputText;
import com.ejb.sfcarchivos.ArchivosFacade;
import com.entidades.sfcarchivos.Archivos;
import org.compras.sfccbdmq.ProveedoresBean;
import org.presupuestos.sfccbdmq.ClasificadorBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.text.DecimalFormat;
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
import org.beans.sfccbdmq.ActivoobligacionFacade;
import org.beans.sfccbdmq.ActivopolizaFacade;
import org.beans.sfccbdmq.ActivosFacade;
import org.beans.sfccbdmq.DepreciacionesFacade;
import org.beans.sfccbdmq.EntidadesFacade;
import org.beans.sfccbdmq.ErogacionesFacade;
import org.beans.sfccbdmq.ExternosFacade;
import org.beans.sfccbdmq.TrackingactivosFacade;
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
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.InsertarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "cambioPolizaActivosSfccbdmq")
@ViewScoped
public class CambioPolizaActivosBean {

    /**
     * Creates a new instance of ActivoBean
     */
    public CambioPolizaActivosBean() {
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
    private ActivoobligacionFacade ejbObligacion;
    @EJB
    private DepreciacionesFacade ejbDepreciaciones;
    @EJB
    private TrackingactivosFacade ejbTracking;
    @EJB
    private ExternosFacade ejbExternos;
    @EJB
    private EntidadesFacade ejbEntidades;
    @EJB
    private ArchivosFacade ejbArchivo;
    @EJB
    private ActivopolizaFacade ejbActivoPoliza;
    private Archivos archivoImagen;
//    private int tamano;
    private String codigoAnterior;
    private Formatos nivel;
    private Activos activo;
    private Activopoliza activoPoliza;
    private Formulario formulario = new Formulario();
    private LazyDataModel<Activos> activos;
    private List<Activopoliza> listaPolizasActivos;
    private List<Activoobligacion> obligaciones;
    private List<Activos> hijos;
    private List<Trackingactivos> tackings;
    private Trackingactivos tacking;
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
    private Codigos estado;
    private Codigos institucion;
    private double valorAnterior;
    private Oficinas oficina;
    private Polizas poliza;
    private Edificios edificio;
    private Externos externoEntidad = new Externos();
    private Entidades entidad;
    private String apellidos;
    private Integer factura;
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

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "CambioPolizaActivosVista";
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

    public String buscar() {
        activos = new LazyDataModel<Activos>() {

            @Override
            public List<Activos> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();

                if (poliza != null) {
                    if (scs.length == 0) {
                        parametros.put(";orden", "o.poliza.descripcion");
                    } else {
                        parametros.put(";orden", "o.poliza." + scs[0].getPropertyName()
                                + (scs[0].isAscending() ? " ASC" : " DESC"));
                    }
                    String where = "  o.activo.baja is null  and o.activo.fechaalta is not null ";
//                String where = "  o.baja is null and o.padre is null and o.alta is not null ";
                    for (Map.Entry e : map.entrySet()) {
                        String clave = (String) e.getKey();
                        String valor = (String) e.getValue();

                        where += " and upper(o." + clave + ") like :" + clave;
                        parametros.put(clave, valor.toUpperCase() + "%");
                    }
                    if (!((codigo == null) || (codigo.isEmpty()))) {
                        where += " and o.activo.codigo like :codigo";
                        parametros.put("codigo", "%" + codigo + "%");
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
//                    if (!((marca == null) || (marca.isEmpty()))) {
//                        where += " and upper(o.activo.marca) like :marca";
//                        parametros.put("marca", marca.toUpperCase() + "%");
//                    }
                    if (!(marca == null)) {
                        where += " and o.activomarca=:activomarca";
                        parametros.put("activomarca", getMarca());
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
                        parametros.put("codigobarras", "%" + codigobarras.toUpperCase() + "%");
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
                    if (factura != null) {
                        where += " and o.activo.factura=:factura";
                        parametros.put("factura", factura);
                    }
                    if (tipo != null) {
                        where += " and o.activo.grupo.tipo=:tipo";
                        parametros.put("tipo", tipo);
                    }
                    if (estado != null) {
                        where += " and o.activo.estado=:estado";
                        parametros.put("estado", estado);
                    }
                    if (clasificacion != null) {
                        where += " and o.activo.clasificacion=:clasificacion";
                        parametros.put("clasificacion", clasificacion);
                    }
                    if (poliza != null) {
                        where += " and o.activo.poliza=:poliza";
                        parametros.put("poliza", poliza);
                    }
                    if (getInstitucion() != null) {
                        where += " and o.activo.externo.institucion=:institucion";
                        parametros.put("institucion", getInstitucion());
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
                        total = ejbActivoPoliza.contar(parametros);
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
                        List<Activopoliza> la = ejbActivoPoliza.encontarParametros(parametros);
                        List<Activos> lista = new LinkedList<>();
                        for (Activopoliza a : la) {
                            lista.add(a.getActivo());
                        }
                        return lista;
                    } catch (ConsultarException ex) {
                        MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                        Logger.getLogger("").log(Level.SEVERE, null, ex);
                    }
                } else {
                    if (scs.length == 0) {
                        parametros.put(";orden", "o.descripcion");
                    } else {
                        parametros.put(";orden", "o." + scs[0].getPropertyName()
                                + (scs[0].isAscending() ? " ASC" : " DESC"));
                    }
                    String where = "  o.baja is null  and o.fechaalta is not null ";
//                String where = "  o.baja is null and o.padre is null and o.alta is not null ";
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
//                    if (!((marca == null) || (marca.isEmpty()))) {
//                        where += " and upper(o.marca) like :marca";
//                        parametros.put("marca", marca.toUpperCase() + "%");
//                    }
                    if (!(marca == null)) {
                        where += " and o.activomarca=:activomarca";
                        parametros.put("activomarca", getMarca());
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
                        parametros.put("codigobarras", "%" + codigobarras.toUpperCase() + "%");
                    }
                    if (!((modelo == null) || (modelo.isEmpty()))) {
                        where += " and upper(o.modelo) like :modelo";
                        parametros.put("modelo", modelo.toUpperCase() + "%");
                    }
                    if (!((numeroserie == null) || (numeroserie.isEmpty()))) {
                        where += " and upper(o.numeroserie) like :numeroserie";
                        parametros.put("numeroserie", numeroserie.toUpperCase() + "%");
                    }
                    if (subgrupoBean.getGrupo() != null) {
                        if (subgrupoBean.getSubGrupo() != null) {
                            where += " and o.subgrupo=:subgrupo";
                            parametros.put("subgrupo", subgrupoBean.getSubGrupo());
                        } else {
                            where += " and o.grupo=:grupo";
                            parametros.put("grupo", subgrupoBean.getGrupo());
                        }
                    }
                    if (factura != null) {
                        where += " and o.factura=:factura";
                        parametros.put("factura", factura);
                    }
                    if (tipo != null) {
                        where += " and o.grupo.tipo=:tipo";
                        parametros.put("tipo", tipo);
                    }
                    if (estado != null) {
                        where += " and o.estado=:estado";
                        parametros.put("estado", estado);
                    }
                    if (clasificacion != null) {
                        where += " and o.clasificacion=:clasificacion";
                        parametros.put("clasificacion", clasificacion);
                    }
                    if (poliza != null) {
                        where += " and o.poliza=:poliza";
                        parametros.put("poliza", poliza);
                    }
                    if (getInstitucion() != null) {
                        where += " and o.externo.institucion=:institucion";
                        parametros.put("institucion", getInstitucion());
                    }
                    if (proveedoresBean.getEmpresa() != null) {
                        where += " and o.proveedor=:proveedor";
                        parametros.put("proveedor", proveedoresBean.getEmpresa().getProveedores());
                    }
                    if (entidadesBean.getEntidad() != null) {
                        where += " and o.custodio=:custodio";
                        parametros.put("custodio", entidadesBean.getEntidad().getEmpleados());
                    }
                    if (getOficinasBean().getEdificio() != null) {
                        if (oficina != null) {
                            where += " and o.localizacion=:localizacion";
                            parametros.put("localizacion", oficina);
                        } else {
                            where += " and o.localizacion.edificio=:edificio";
                            parametros.put("edificio", getOficinasBean().getEdificio());
                        }
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
                    if (control == 1) {
                        where += " and o.control=true ";
                    } else if (control == -1) {
                        where += " and o.control=false ";
                    }
                    int total = 0;

                    try {
                        parametros.put(";where", where);
                        total = ejbActivos.contar(parametros);
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
                        return ejbActivos.encontarParametros(parametros);
                    } catch (ConsultarException ex) {
                        MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                        Logger.getLogger("").log(Level.SEVERE, null, ex);
                    }
                }
                return null;
            }
        };
        return null;
    }

    public String pone() {
        activo = (Activos) activos.getRowData();
        entidad = null;
        apellidos = null;
        imagenesBean.setIdModulo(activo.getId());
        imagenesBean.setModulo("ACTIVO");
        imagenesBean.Buscar();
        valorAnterior = activo.getValoralta().doubleValue();
        Map parametros = new HashMap();
        parametros.put(";where", "o.padre=:padre and o.baja is null");
        parametros.put("padre", activo);
        DecimalFormat df = new DecimalFormat("###,###,###,##0.00");
        activoPoliza = new Activopoliza();
        activoPoliza.setActivo(activo);
        activoPoliza.setDesde(new Date());

        try {
            hijos = ejbActivos.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put(";where", "o.activo=:padre");
            parametros.put("padre", activo);
            tackings = ejbTracking.encontarParametros(parametros);
            listaPolizasActivos = ejbActivoPoliza.encontarParametros(parametros);
            obligaciones = ejbObligacion.encontarParametros(parametros);
            depreciaciones = ejbDepreciaciones.encontarParametros(parametros);
            listadoErogaciones = ejbErogaciones.encontarParametros(parametros);
            setTacking(new Trackingactivos());
            getTacking().setActivo(activo);
//            String custodio = "Valor Actual : " + df.format(valorAnterior);
//            tacking.setDescripcion(custodio);
            getTacking().setFecha(new Date());
            getTacking().setTipo(5);
            getTacking().setUsuario(seguridadbean.getLogueado().getUserid());
            parametros = new HashMap();
            parametros.put(";where", "o.imagen.modulo='ACTIVOFOTO' and o.imagen.idmodulo=:activo");
            parametros.put("activo", activo.getId());
            archivoImagen = null;
            List<Archivos> larch = ejbArchivo.encontarParametros(parametros);
            for (Archivos a : larch) {
                archivoImagen = a;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.advertencia(ex.getMessage());
            Logger.getLogger(BajasActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.editar();
        return null;
    }

//    public String saca() {
//        activo = (Activos) activos.getRowData();
//        entidad = null;
//        apellidos = null;
//        imagenesBean.setIdModulo(activo.getId());
//        imagenesBean.setModulo("ACTIVO");
//        imagenesBean.Buscar();
//        valorAnterior = activo.getValoralta().doubleValue();
//        Map parametros = new HashMap();
//        parametros.put(";where", "o.padre=:padre and o.baja is null");
//        parametros.put("padre", activo);
//        DecimalFormat df = new DecimalFormat("###,###,###,##0.00");
//        try {
//            hijos = ejbActivos.encontarParametros(parametros);
//            parametros = new HashMap();
//            parametros.put(";where", "o.activo=:padre");
//            parametros.put("padre", activo);
//            tackings = ejbTracking.encontarParametros(parametros);
//            obligaciones = ejbObligacion.encontarParametros(parametros);
//            depreciaciones = ejbDepreciaciones.encontarParametros(parametros);
//            listadoErogaciones = ejbErogaciones.encontarParametros(parametros);
//            setTacking(new Trackingactivos());
//            getTacking().setActivo(activo);
//            String custodio = "Exlusión de póliza : " + activo.getPoliza().getDescripcion();
//            getTacking().setDescripcion(custodio);
//            getTacking().setFecha(new Date());
//            getTacking().setTipo(6);
//            getTacking().setUsuario(seguridadbean.getLogueado().getUserid());
//            parametros = new HashMap();
//            parametros.put(";where", "o.imagen.modulo='ACTIVOFOTO' and o.imagen.idmodulo=:activo");
//            parametros.put("activo", activo.getId());
//            archivoImagen = null;
//            List<Archivos> larch = ejbArchivo.encontarParametros(parametros);
//            for (Archivos a : larch) {
//                archivoImagen = a;
//            }
//            parametros = new HashMap();
//            parametros.put(";where", "o.poliza=:poliza and o.activo=:activo");
//            parametros.put("activo", activo);
//            parametros.put("poliza", activo.getPoliza());
//            List<Activopoliza> lpz = ejbActivoPoliza.encontarParametros(parametros);
//            for (Activopoliza ap : lpz) {
//                activoPoliza = ap;
//            }
//            if (activoPoliza == null) {
//                MensajesErrores.advertencia("No existe informacion de inclusión en la póliza");
//                return null;
//            }
//        } catch (ConsultarException ex) {
//            MensajesErrores.advertencia(ex.getMessage());
//            Logger.getLogger(BajasActivosBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        formulario.eliminar();
//        return null;
//    }
    private boolean validar() {
//        if ((activo.getPoliza() == null)) {
//            MensajesErrores.advertencia("Póliza es obligatorio");
//            return true;
//        }
        if (activoPoliza.getValorasegurado() == null) {
            MensajesErrores.advertencia("ingrese el valor de la prima");
            return true;
        }
        if (activoPoliza.getValorasegurado().doubleValue() == 0) {
            MensajesErrores.advertencia("ingrese el valor de la prima");
            return true;
        }
        if (activoPoliza.getDesde() == null) {
            MensajesErrores.advertencia("ingrese el una fecha de incio de cobertura");
            return true;
        }
        if (activoPoliza.getDesde().before(activoPoliza.getPoliza().getDesde())) {
            MensajesErrores.advertencia("ingrese el una fecha de incio de cobertura mayor o igual a inico de póliza");
            return true;
        }
        for (Activopoliza a : listaPolizasActivos) {
            if (a.getPoliza().equals(activoPoliza.getPoliza())) {
                MensajesErrores.advertencia("Ya esta incluido el activo en la poliza");
                return true;
            }
        }
        return false;
    }

    public String borrar(Activopoliza acpo) {

        try {
//            activo.set(null);
//            ejbActivos.edit(activo, getSeguridadbean().getLogueado().getUserid());
            setTacking(new Trackingactivos());
            getTacking().setActivo(acpo.getActivo());
            String custodio = "Exlusión de póliza : " + acpo.getActivo().getDescripcion();
            getTacking().setDescripcion(custodio);
            getTacking().setCuenta1(null);
            getTacking().setCuenta2(custodio);
            getTacking().setFecha(new Date());
            getTacking().setTipo(-6);
            getTacking().setUsuario(seguridadbean.getLogueado().getUserid());
            ejbTracking.create(getTacking(), getSeguridadbean().getLogueado().getUserid());

            for (Activos a : hijos) {
                Map parametros = new HashMap();
                parametros.put(";where", "o.poliza=:poliza and o.activo=:activo");
                parametros.put("activo", a);
                parametros.put("poliza", acpo.getPoliza());
                List<Activopoliza> lpz = ejbActivoPoliza.encontarParametros(parametros);
//                Activopoliza ap1=new Activopoliza();
                for (Activopoliza ap : lpz) {
                    ejbActivoPoliza.remove(ap, getSeguridadbean().getLogueado().getUserid());
                }
//                ejbActivos.edit(a, getSeguridadbean().getLogueado().getUserid());
            }
            ejbActivoPoliza.remove(acpo, getSeguridadbean().getLogueado().getUserid());
            Map parametros = new HashMap();
            parametros.put(";where", "o.activo=:padre");
            parametros.put("padre", activo);
            tackings = ejbTracking.encontarParametros(parametros);
            listaPolizasActivos = ejbActivoPoliza.encontarParametros(parametros);

        } catch (BorrarException | InsertarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
//        formulario.cancelar();
//        buscar();
        MensajesErrores.informacion("Se retiro activo de póliza");
        return null;
    }

    public String grabar() {
        if (validar()) {
            return null;
        }

        try {
            activoPoliza.setActivo(activo);
            setTacking(new Trackingactivos());
            getTacking().setActivo(activoPoliza.getActivo());
            String custodio = "Póliza : " + activoPoliza.getActivo().getDescripcion();
            getTacking().setDescripcion(custodio);
            getTacking().setCuenta1(null);
            getTacking().setCuenta2(custodio);
            getTacking().setFecha(new Date());
            getTacking().setTipo(6);
            getTacking().setUsuario(seguridadbean.getLogueado().getUserid());
            activoPoliza.setHasta(activoPoliza.getPoliza().getHasta());
//            activoPoliza.setPoliza(activo.getPoliza());
//            ejbActivos.edit(activo, getSeguridadbean().getLogueado().getUserid());
            ejbActivoPoliza.create(activoPoliza, getSeguridadbean().getLogueado().getUserid());
            ejbTracking.create(getTacking(), getSeguridadbean().getLogueado().getUserid());
            for (Activos a : hijos) {
//                a.setPoliza(activo.getPoliza());
//                ejbActivos.edit(a, getSeguridadbean().getLogueado().getUserid());
                Activopoliza ap = new Activopoliza();
                ap.setActivo(a);
                ap.setDesde(new Date());
                ap.setHasta(activoPoliza.getPoliza().getHasta());
                ap.setPoliza(activoPoliza.getPoliza());
                ejbActivoPoliza.create(ap, getSeguridadbean().getLogueado().getUserid());
            }
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        listaPolizasActivos.add(activoPoliza);
        activoPoliza = new Activopoliza();
        activoPoliza.setDesde(new Date());
//        formulario.cancelar();
//        buscar();
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

    public List<Activos> getHijosFuera() {
        if (activos == null) {
            return null;
        }
        if (!activos.isRowAvailable()) {
            return null;
        }
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
     * @return the activoPoliza
     */
    public Activopoliza getActivoPoliza() {
        return activoPoliza;
    }

    /**
     * @param activoPoliza the activoPoliza to set
     */
    public void setActivoPoliza(Activopoliza activoPoliza) {
        this.activoPoliza = activoPoliza;
    }

    /**
     * @return the tacking
     */
    public Trackingactivos getTacking() {
        return tacking;
    }

    /**
     * @param tacking the tacking to set
     */
    public void setTacking(Trackingactivos tacking) {
        this.tacking = tacking;
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
     * @return the listaPolizasActivos
     */
    public List<Activopoliza> getListaPolizasActivos() {
        return listaPolizasActivos;
    }

    /**
     * @param listaPolizasActivos the listaPolizasActivos to set
     */
    public void setListaPolizasActivos(List<Activopoliza> listaPolizasActivos) {
        this.listaPolizasActivos = listaPolizasActivos;
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
