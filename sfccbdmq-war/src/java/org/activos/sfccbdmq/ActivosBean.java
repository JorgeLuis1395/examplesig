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
import org.compras.sfccbdmq.ProveedoresBean;
import org.presupuestos.sfccbdmq.ClasificadorBean;
import org.presupuestos.sfccbdmq.ProyectosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.text.DecimalFormat;
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
import javax.faces.application.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.imageio.ImageIO;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeFactory;
import org.auxiliares.sfccbdmq.AuxiliarCarga;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Constantes;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.beans.sfccbdmq.ActasFacade;
import org.beans.sfccbdmq.ActasactivosFacade;
import org.beans.sfccbdmq.ActivoobligacionFacade;
import org.beans.sfccbdmq.ActivosFacade;
import org.beans.sfccbdmq.CodigosFacade;
import org.beans.sfccbdmq.DepreciacionesFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.EntidadesFacade;
import org.beans.sfccbdmq.ErogacionesFacade;
import org.beans.sfccbdmq.ExternosFacade;
import org.beans.sfccbdmq.FormatosFacade;
import org.beans.sfccbdmq.GrupoactivosFacade;
import org.beans.sfccbdmq.SubgruposactivosFacade;
import org.beans.sfccbdmq.TrackingactivosFacade;
import org.entidades.sfccbdmq.Actas;
import org.entidades.sfccbdmq.Actasactivos;
import org.entidades.sfccbdmq.Activoobligacion;
import org.entidades.sfccbdmq.Activos;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Conceptos;
import org.entidades.sfccbdmq.Contratos;
import org.entidades.sfccbdmq.Depreciaciones;
import org.entidades.sfccbdmq.Edificios;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Entidades;
import org.entidades.sfccbdmq.Erogaciones;
import org.entidades.sfccbdmq.Externos;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Grupoactivos;
import org.entidades.sfccbdmq.Marcas;
import org.entidades.sfccbdmq.Oficinas;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Polizas;
import org.entidades.sfccbdmq.Subgruposactivos;
import org.entidades.sfccbdmq.Tipoactivo;
import org.entidades.sfccbdmq.Trackingactivos;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.icefaces.ace.component.fileentry.FileEntry;
import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.icefaces.ace.component.fileentry.FileEntryResults;
import org.icefaces.ace.event.TextChangeEvent;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.CodigosBean;
import org.talento.sfccbdmq.RolEmpleadoBean;
import org.utilitarios.sfccbdmq.Recurso;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "activosSfccbdmq")
@ViewScoped
public class ActivosBean {

    /**
     * Creates a new instance of ActivoBean
     */
    public ActivosBean() {
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
    private FormatosFacade ejbFormatos;
    @EJB
    private ActivosFacade ejbActivos;
    @EJB
    private DepreciacionesFacade ejbDepreciaciones;
    @EJB
    private TrackingactivosFacade ejbTracking;
    @EJB
    private GrupoactivosFacade ejbGrupos;
    @EJB
    private ActivoobligacionFacade ejbObligacion;
    @EJB
    private EntidadesFacade ejbEntidades;
    @EJB
    private ImagenesFacade ejbimagenes;
    @EJB
    private ArchivosFacade ejbArchivo;
    @EJB
    private SubgruposactivosFacade ejbSubGrupos;
    @EJB
    private ExternosFacade ejbExternos;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private ActasactivosFacade ejbActasactivos;
    @EJB
    private ActasFacade ejbActas;
    @EJB
    private CodigosFacade ejbCodigos;
    private Archivos archivoImagen;
//    private int tamano;
    private Imagenes imagen;

    private String codigoAnterior;
    private Formatos nivel;
    private Activos activo;
    private Formulario formulario = new Formulario();
    // autocompletar paar que ponga el custodio
    private Entidades entidad;
    private String apellidos;
    private List listaUsuarios;
//    private List listaExternos;
    private boolean esExterno = false;
    private Externos externoEntidad = new Externos();
    private List<Entidades> listaEntidades;
    private List<Externos> listaExternos;
    //
    private LazyDataModel<Activos> activos;
    private List<Activoobligacion> obligaciones;
    private List<Activos> hijos;
    private List<Trackingactivos> tackings;
    private List<Activos> listaActivosReporte;
    private List<Activos> listaActivosExportar;
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
    private Activos padre;
    private String externo;
    private Tipoactivo tipo;
//    private Integer factura;
    private String factura;
    private Contratos contrato;
    private Grupoactivos grupo;
    private Codigos clasificacion;
    private Codigos estado;
    private Codigos institucion;
    private Oficinas oficina;
    private Edificios edificio;
    private Polizas poliza;
    private List listaActivo;
    private int garantia;
    private int operativo;
    private int alta = 3;
    private int tipoActivo = 3;
    private String tipoAuxiliar;
    private String nominativo;
    private Date fechaIngreso;
    private double valor;
    private int f1 = 0;
    private int f2 = 0;
    private int f3 = 0;
    private int tipoCompra = 0;
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
    @ManagedProperty(value = "#{proyectosSfccbdmq}")
    private ProyectosBean proyectosBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{actasActivosSfccbdmq}")
    private ActasActivosBean actasActivosBean;
    private Perfiles perfil;
    private Formulario formularioImprimir = new Formulario();
    private Recurso reportepdf;
    private String nombrepdf;
    private DocumentoPDF pdf;
    private Entidades responsable;
    private String numeroActa;
    private Resource reporte;
    private Formulario formularioReporte = new Formulario();

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
        String nombreForma = "RegistroActivosVista";
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
                if (scs.length == 0) {
                    parametros.put(";orden", "o.id desc");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }
                String where = "  o.baja is null ";
//                String where = "  o.baja is null and o.control=false and o.padre is null ";
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
                    parametros.put("codigobarras", "%" + codigobarras.toUpperCase() + "%");
                }
                if (!(padre == null)) {
                    where += " and o.padre=:padre";
                    parametros.put("padre", padre);
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
                if (getAlta() == 0) {
                    where += " and o.fechaalta is null";
                } else if (getAlta() == 1) {
                    where += " and o.fechaalta is not null";
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
                if (!((factura == null) || (factura.isEmpty()))) {
                    where += " and upper(o.factura) like :factura";
                    parametros.put("factura", "%" + getFactura().toUpperCase() + "%");
                }
                if (getTipoActivo() == 1) {
                    where += " and o.control=true ";//tipo activo = 1 = bien control
                } else if (getTipoActivo() == 0) {
                    where += " and o.control=false ";//tipo activo = 0 = activo fijo
                }
                if (!((tipoAuxiliar == null) || (tipoAuxiliar.isEmpty()))) {
                    where += " and o.tipoauxiliar=:tipoauxiliar";
                    parametros.put("tipoauxiliar", tipoAuxiliar);
                }
                if (!((nominativo == null) || (nominativo.isEmpty()))) {
                    where += " and o.nominativo=:nominativo";
                    parametros.put("nominativo", nominativo);
                }
                if (!(contrato == null)) {
                    where += " and o.contrato=:contrato";
                    parametros.put("contrato", contrato);
                }
                if (!(fechaIngreso == null)) {
                    where += " and o.fechaingreso=:fechaingreso";
                    parametros.put("fechaingreso", fechaIngreso);
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

    public String nuevo() {
//        if (tipoActivo == 3) {
//            MensajesErrores.advertencia("Seleccione un Tipo de activo");
//            return null;
//        }
        tipoActivo = 0;
        activo = new Activos();
        formulario.insertar();
        archivoImagen = null;
        imagen = null;
        entidad = null;
        apellidos = null;
        listaUsuarios = null;
        esExterno = false;
        f1 = 0;
        f2 = 0;
        f3 = 0;
        externoEntidad = new Externos();
        proyectosBean.setTipoBuscar("-1");
        proyectosBean.setProyectoSeleccionado(null);
        activo.setFechapuestaenservicio(new Date());
        activo.setFechaingreso(new Date());
        return null;
    }

    public String nuevoComponente() {
        proyectosBean.setTipoBuscar("-1");
        proyectosBean.setProyectoSeleccionado(null);
        activo = new Activos();
        Activos padre = (Activos) activos.getRowData();
        activo.setPadre(padre);
        activo.setGrupo(padre.getGrupo());
        activo.setClasificacion(padre.getClasificacion());
        activo.setVidautil(padre.getVidautil());
        activo.setValorresidual(padre.getValorresidual());
        activo.setControl(padre.getControl());
        if (!padre.getControl()) {
            activo.setFechapuestaenservicio(new Date());
            activo.setFechaingreso(new Date());
        }
        formulario.insertar();
        archivoImagen = null;
        imagen = null;
        entidad = null;
        apellidos = null;
        listaUsuarios = null;
        esExterno = false;
        externoEntidad = new Externos();
        subgrupoBean.setGrupo(grupo);
        subgrupoBean.getComboSubGrupo();
        return null;
    }

    public String modificaHijo(Activos activo) {
//        if (!menusBean.getPerfil().getModificacion()) {
//            MensajesErrores.advertencia("No tiene autorización para modificar un registro");
//            return null;
//        }
        proyectosBean.setTipoBuscar("-1");
        this.activo = activo;
        imagenesBean.setIdModulo(activo.getId());
        imagenesBean.setModulo("ACTIVO");
        imagenesBean.Buscar();
        Map parametros = new HashMap();
        parametros.put(";where", "o.padre=:padre and o.baja is null");
        parametros.put("padre", activo);
        try {
//            setHijos(ejbActivos.encontarParametros(parametros));
            parametros = new HashMap();
            parametros.put(";where", "o.activo=:padre");
            parametros.put("padre", activo);
            setTackings(ejbTracking.encontarParametros(parametros));
            setObligaciones(ejbObligacion.encontarParametros(parametros));
            setDepreciaciones(ejbDepreciaciones.encontarParametros(parametros));
            if (!((activo.getCcosto() == null) || (activo.getCcosto().isEmpty()))) {
                proyectosBean.setProyectoSeleccionado(proyectosBean.traerCodigo(activo.getCcosto()));
                if (proyectosBean.getProyectoSeleccionado() == null) {
                    activo.setCcosto(null);
                }
            }
            // taer la foto
            parametros = new HashMap();
            parametros.put(";where", "o.imagen.modulo='ACTIVOFOTO' and o.imagen.idmodulo=:activo");
            parametros.put("activo", activo.getId());
            archivoImagen = null;
            imagen = null;
            List<Archivos> larch = ejbArchivo.encontarParametros(parametros);
            for (Archivos a : larch) {
                archivoImagen = a;
                imagen = a.getImagen();
            }
            if (activo.getExterno() == null) {
                esExterno = false;
                setExternoEntidad(new Externos());
            } else {
                esExterno = true;
                setExternoEntidad(activo.getExterno());
            }

        } catch (ConsultarException ex) {
            MensajesErrores.advertencia(ex.getMessage());
            Logger.getLogger(BajasActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.editar();
        return null;
    }

    public String modifica() {
//        if (!menusBean.getPerfil().getModificacion()) {
//            MensajesErrores.advertencia("No tiene autorización para modificar un registro");
//            return null;
//        }
        activo = (Activos) activos.getRowData();
        if (activo.getControl()) {
            tipoActivo = 1;
        } else {
            tipoActivo = 0;
        }
        activo.setValorAltaSinIva(BigDecimal.ZERO);
        if (activo.getIva() == null) {
            activo.setIva(BigDecimal.ZERO);
        }
        if (!(activo.getIva() == null) || (activo.getIva().equals(0.00))) {
            double nv = (activo.getValoralta().doubleValue()) / (activo.getIva().doubleValue() + 1);
            activo.setValorAltaSinIva(new BigDecimal(nv).setScale(2, RoundingMode.HALF_UP));
        } else {
            activo.setValorAltaSinIva(activo.getValoralta());
        }

        proyectosBean.setTipoBuscar("-1");
        if (!((activo.getCcosto() == null) || (activo.getCcosto().isEmpty()))) {
            proyectosBean.setProyectoSeleccionado(proyectosBean.traerCodigo(activo.getCcosto()));
            if (proyectosBean.getProyectoSeleccionado() == null) {
                activo.setCcosto(null);
            }
        }
        if (activo.getProveedor() != null) {
            proveedoresBean.setProveedor(activo.getProveedor());
            proveedoresBean.setEmpresa(activo.getProveedor().getEmpresa());
            proveedoresBean.setRuc(activo.getProveedor().getEmpresa().getRuc());
            String[] registro = getActivo().getFactura().split("#");
            for (String fila : registro) {
                String[] campos = fila.split("-");
                AuxiliarCarga a = new AuxiliarCarga();
                a.setTotal(campos[0]);
                a.setAuxiliar(campos[1]);
                a.setCuenta(campos[2]);
                f1 = Integer.parseInt(a.getTotal());
                f2 = Integer.parseInt(a.getAuxiliar());
                f3 = Integer.parseInt(a.getCuenta());
                if (f1 == 999 && f2 == 999) {
                    tipoCompra = 0;
                } else {
                    tipoCompra = 1;
                }
            }
        }
        if (activo.getAlta() != null) {
            if (activo.getAlta().getTipo() == 0) {
                if (activo.getProveedor() != null) {
                    proveedoresBean.setEmpresa(activo.getProveedor().getEmpresa());
                    proveedoresBean.setTipobuscar("-1");
                    proveedoresBean.setRuc(activo.getProveedor().getEmpresa().getRuc());

                }
            } else {
                proveedoresBean.setEmpresa(null);
                proveedoresBean.setRuc(null);
//                    proveedoresBean.setTipobuscar(null);
                activo.setProveedor(null);
            }
        }

        //Colocar el numero de serie y el codigo de barras si esta en null
        if (activo.getNumeroserie() == null || activo.getNumeroserie().isEmpty()) {
            activo.setNumeroserie(activo.getCodigo());
        }
        if (activo.getBarras() == null || activo.getBarras().isEmpty()) {
            activo.setBarras(activo.getCodigo());
        }
        imagenesBean.setIdModulo(activo.getId());
        imagenesBean.setModulo("ACTIVO");
        imagenesBean.Buscar();
        Map parametros = new HashMap();
        parametros.put(";where", "o.padre=:padre and o.baja is null");
        parametros.put("padre", activo);
        try {
            setHijos(ejbActivos.encontarParametros(parametros));
            parametros = new HashMap();
            parametros.put(";where", "o.activo=:padre");
            parametros.put("padre", activo);
            setTackings(ejbTracking.encontarParametros(parametros));
            setObligaciones(ejbObligacion.encontarParametros(parametros));
            setDepreciaciones(ejbDepreciaciones.encontarParametros(parametros));
            setListadoErogaciones(ejbErogaciones.encontarParametros(parametros));

            parametros = new HashMap();
            parametros.put(";where", "o.imagen.modulo='ACTIVOFOTO' and o.imagen.idmodulo=:activo");
            parametros.put("activo", activo.getId());
            archivoImagen = null;
            imagen = null;
            List<Archivos> larch = ejbArchivo.encontarParametros(parametros);
            for (Archivos a : larch) {
                archivoImagen = a;
                imagen = a.getImagen();
            }
            if (activo.getExterno() == null) {
                esExterno = false;
                setExternoEntidad(new Externos());
                if (tipoActivo == 0) {
                    externoEntidad.setEdificio(new Edificios());
                }
            } else {
                esExterno = true;
                setExternoEntidad(activo.getExterno());
                if (externoEntidad == null) {
                    setExternoEntidad(activo.getExterno());
                    if (tipoActivo == 0) {
                        externoEntidad.setEdificio(new Edificios());
                    }
                }
            }

        } catch (ConsultarException ex) {
            MensajesErrores.advertencia(ex.getMessage());
            Logger.getLogger(BajasActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.editar();
        return null;
    }

    public String elimina() {
//        if (!menusBean.getPerfil().getBorrado()) {
//            MensajesErrores.advertencia("No tiene autorización para borrar un registro");
//            return null;
//        }

        activo = (Activos) activos.getRowData();
        formulario.eliminar();
        return null;
    }

    private boolean validar() {
//        if ((activo.getCodigo() == null) || (activo.getCodigo().isEmpty())) {
//            MensajesErrores.advertencia("Código es Obligatorio es obligatorio");
//            return true;
//        }
//        if ((activo.getDescripcion() == null) || (activo.getDescripcion().isEmpty())) {
//            MensajesErrores.advertencia("Descripcion es obligatoria");
//            return true;
//        }

        if (((activo.getNombre() == null) || (activo.getNombre().isEmpty()))) {
            MensajesErrores.advertencia("Nombre es obligatorio");
            return true;
        }
//        if (((activo.getMarca() == null) || (activo.getMarca().isEmpty()))) {
//            MensajesErrores.advertencia("Marca es obligatoria");
//            return true;
//        }
        if (activo.getActivomarca() == null) {
            MensajesErrores.advertencia("Marca es obligatoria");
            return true;
        }

        if (((activo.getModelo() == null) || (activo.getModelo().isEmpty()))) {
            MensajesErrores.advertencia("Modelo es obligatoria");
            return true;
        }

        if (activo.getGrupo() == null) {
            MensajesErrores.advertencia("Grupo es obligatoria");
            return true;
        }

        if (activo.getSubgrupo() == null) {
            MensajesErrores.advertencia("Sub Grupo es obligatorio");
            return true;
        }
        if (tipo != null) {
            MensajesErrores.advertencia("Tipo es obligatorio");
            return true;
        }
//        if (activo.getEstado() == null) {
//            MensajesErrores.advertencia("Estado es obligatorio");
//            return true;
//        }
        if (activo.getClasificacion() == null) {
            MensajesErrores.advertencia("Clasificación es obligatoria");
            return true;
        }
//        retirada la validación de custodio
//        if (esExterno) {
//            if ((externoEntidad.getEdificio() == null)) {
//                MensajesErrores.advertencia("Edificio es obligatorio");
//                return true;
//            }
//            if ((externoEntidad.getEmpresa() == null) || (externoEntidad.getEmpresa().isEmpty())) {
//                MensajesErrores.advertencia("RUC Empresa es obligatoria");
//                return true;
//            }
//            if ((externoEntidad.getNombre() == null) || (externoEntidad.getNombre().isEmpty())) {
//                MensajesErrores.advertencia("Nombre es obligatorio");
//                return true;
//            }
//            activo.setExterno(externoEntidad);
//            activo.setCustodio(null);
//        } else {
//            if (entidad == null) {
//                MensajesErrores.advertencia("Custodio es necesario");
//                return true;
//            }
//            if (entidad.getEmpleados() == null) {
//                MensajesErrores.advertencia("Custodio, no es empleado");
//                return true;
//            }
//            activo.setExterno(null);
//            activo.setCustodio(entidad.getEmpleados());
//        }
        // buscar para no tener repetido
//        Map parametros = new HashMap();
//        parametros.put(";where", "o.codigo=:codigo");
//        parametros.put("codigo", activo.getCodigo());
//        try {
//            int total = ejbActivos.contar(parametros);
//            if (total > 0) {
//                MensajesErrores.advertencia("Código ya existe");
//                return true;
//            }
//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(ActivosBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
        if (activo.getValoralta() == null) {
            MensajesErrores.advertencia("Valor de alta es obligatorio");
            return true;
        }
        if (tipoActivo == 0) {
            if (activo.getIva() == null) {
                MensajesErrores.advertencia("Valor de iva es obligatorio");
                return true;
            }
        }
//        if (activo.getIva().doubleValue() <= 0) {
//            MensajesErrores.advertencia("Valor de iva es obligatorio");
//            return true;
//        }
        if (proveedoresBean.getEmpresa() != null) {
            if (tipoCompra == 3) {
                MensajesErrores.advertencia("Seleccione un Tipo de Compra");
                return true;
            }
            if (tipoCompra == 0) {
                if (f1 == 0 || f2 == 0 || f3 == 0) {
                    MensajesErrores.advertencia("Factura es obligatorio");
                    return true;
                }
            }
            if (tipoCompra == 1) {
                if (f3 == 0) {
                    MensajesErrores.advertencia("Factura es obligatorio");
                    return true;
                }
            }
        }

        return false;
    }

    public String insertar() {
        if (tipoActivo == 3) {
            MensajesErrores.advertencia("Seleccione un Tipo de activo");
            return null;
        }
        //tipo activo = 1 = bien control
        //tipo activo = 0 = activo fijo
        if (getTipoActivo() == 0) {
            activo.setControl(false);
            activo.setFechapuestaenservicio(new Date());
            if (proyectosBean.getProyectoSeleccionado() != null) {
                activo.setCcosto(proyectosBean.getProyectoSeleccionado().getCodigo());
            }
            activo.setValorreposicion(activo.getValoralta());
            activo.setValoradquisiscion(activo.getValoralta());
        }
        if (getTipoActivo() == 1) {
            activo.setControl(true);
            activo.setFechapuestaenservicio(null);
        }
        if (validar()) {
            return null;
        }

        try {
            Integer numero = activo.getGrupo().getSecuencia();
            numero++;
            Trackingactivos t = new Trackingactivos();
            DecimalFormat df = new DecimalFormat("00000");
//            DecimalFormat df = new DecimalFormat("0000000000");
//            *************************************incrementar para externo********
            if (esExterno) {
                // es externo
                if (externoEntidad.getEmpresa() == null || externoEntidad.getEmpresa().isEmpty()) {
                    MensajesErrores.advertencia("Favor ingresar la cédula del custodio");
                    return null;
                }
                if (externoEntidad.getId() == null) {
                    ejbExternos.create(externoEntidad, getSeguridadbean().getLogueado().getUserid());
                } else {
                    ejbExternos.edit(externoEntidad, getSeguridadbean().getLogueado().getUserid());
                }
                activo.setExterno(externoEntidad);
            }

            if (activo.getAlta() != null) {
                if (activo.getAlta().getTipo() == 0) {
                    if (proveedoresBean.getEmpresa() != null) {
                        activo.setProveedor(proveedoresBean.getEmpresa().getProveedores());
                        DecimalFormat dfact = new DecimalFormat("000");
                        if (tipoCompra == 0) { //interna
                            activo.setFactura(dfact.format(f1) + "-" + dfact.format(f2) + "-" + f3);
                        } else { //externa
                            activo.setFactura("999-999-" + f3);
                        }
                    }
                } else {
                    activo.setProveedor(null);
                }
            }
            if (activo.getPadre() == null) {
                activo.setDepreciable(activo.getGrupo().getDepreciable());
                activo.setVidautil(activo.getGrupo().getVidautil());
                activo.setValorresidual(activo.getValorresidual());
            }
            ejbActivos.create(activo, getSeguridadbean().getLogueado().getUserid());
            activo.setCodigo(df.format(activo.getId()));
//            ejbActivos.edit(activo, getSeguridadbean().getLogueado().getUserid());
            if (activo.getNumeroserie() == null || activo.getNumeroserie().isEmpty()) {
                activo.setNumeroserie(activo.getCodigo());
            }
            if (activo.getBarras() == null || activo.getBarras().isEmpty()) {
                activo.setBarras(activo.getCodigo());
            }

            if (archivoImagen != null) {
                imagen.setModulo("ACTIVOFOTO");
                imagen.setIdmodulo(activo.getId());
                ejbimagenes.create(imagen);
                archivoImagen.setImagen(imagen);
                ejbArchivo.create(archivoImagen);
            }
            t.setActivo(activo);
            t.setDescripcion("CREACION DE ACTIVO");
            t.setFecha(new Date());
            t.setTipo(2);
            if (tipoActivo == 0) {
                t.setCuenta1(null);
                if (activo.getExterno() == null) {
                    t.setCuenta2(activo.getCustodio() == null ? "SIN CUSTODIO" : activo.getCustodio().getEntidad().toString());
                } else {
                    t.setCuenta2(activo.getExterno() == null ? "SIN CUSTODIO" : activo.getExterno().getInstitucion().getNombre() + " " + activo.getExterno().getNombre());
                }
            }
            if (tipoActivo == 1) {
                t.setCuenta1("");
                t.setCuenta2("");
            }
            t.setUsuario(getSeguridadbean().getLogueado().getUserid());
            t.setValor(activo.getValoralta() == null ? 0 : activo.getValoralta().floatValue());
            t.setValornuevo(activo.getValoradquisiscion() == null ? 0 : activo.getValoradquisiscion().floatValue());
            ejbTracking.create(t, getSeguridadbean().getLogueado().getUserid());
            //Número de acta
            if (activo.getPadre() == null) {
                Codigos tipoActa = codigosBean.traerCodigo(Constantes.ACTAS, "06");
                if (tipoActa == null) {
                    MensajesErrores.advertencia("No existe tipo de acta de código 06");
                    return null;
                }
                Codigos configuracion = codigosBean.traerCodigo(Constantes.CONFIGURACION_ACTAS, "06");
                if (configuracion == null) {
                    MensajesErrores.advertencia("No existe configuración de acta de código 06");
                    return null;
                }
                Actas actaNueva = new Actas();
                actaNueva.setAceptacion(configuracion.getNombre());
                actaNueva.setAntecedentes(configuracion.getDescripcion());
                actaNueva.setTipo(tipoActa);
                actaNueva.setFecha(new Date());
                String numeroActaActivo = tipoActa.getParametros();
                if ((numeroActaActivo == null) || (numeroActaActivo.isEmpty())) {
                    numeroActaActivo = "1";
                }
                int num = Integer.parseInt(numeroActaActivo);
                Date fecha = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                DecimalFormat df1 = new DecimalFormat("000000");
                actaNueva.setNumero(num);
                actaNueva.setNumerotexto(sdf.format(fecha) + df1.format(num));
                actaNueva.setReciben(seguridadbean.getLogueado().getUserid());
                actaNueva.setFecha(new Date());
                Integer numeroNumevo = num + 1;
                tipoActa.setParametros("" + numeroNumevo);
                ejbCodigos.edit(tipoActa, seguridadbean.getLogueado().getUserid());
                ejbActas.create(actaNueva, seguridadbean.getLogueado().getUserid());
                Actasactivos aa = new Actasactivos();
                aa.setActa(actaNueva);
                aa.setActivo(activo);
                ejbActasactivos.create(aa, seguridadbean.getLogueado().getUserid());
                Grupoactivos g = activo.getGrupo();
                g.setSecuencia(numero);
                ejbGrupos.edit(g, getSeguridadbean().getLogueado().getUserid());
                actasActivosBean.imprime(actaNueva);
                Integer numeroActaAlta = Integer.parseInt(actaNueva.getNumerotexto());
                activo.setActa(numeroActaAlta);
            }
            ejbActivos.edit(activo, getSeguridadbean().getLogueado().getUserid());
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
//        reporte(activo);
        buscar();
        f1 = 0;
        f2 = 0;
        f3 = 0;
        return null;
    }

    public String grabar() {
        if (tipoActivo == 3) {
            MensajesErrores.advertencia("Seleccione un Tipo de activo");
            return null;
        }
        //tipo activo = 1 = bien control
        //tipo activo = 0 = activo fijo
        if (getTipoActivo() == 0) {
            activo.setControl(false);
            activo.setFechapuestaenservicio(new Date());
            if (proyectosBean.getProyectoSeleccionado() != null) {
                activo.setCcosto(proyectosBean.getProyectoSeleccionado().getCodigo());
            }
            activo.setValorreposicion(activo.getValoralta());
            activo.setValoradquisiscion(activo.getValoralta());
        }
        if (getTipoActivo() == 1) {
            activo.setControl(true);
            activo.setFechapuestaenservicio(null);
        }
        try {
            if (proyectosBean.getProyectoSeleccionado() != null) {
                activo.setCcosto(proyectosBean.getProyectoSeleccionado().getCodigo());
            }
            if (activo.getAlta() != null) {
                if (activo.getAlta().getTipo() == 0) {
                    if (proveedoresBean.getEmpresa() != null) {
                        activo.setProveedor(proveedoresBean.getEmpresa().getProveedores());
                        DecimalFormat dfact = new DecimalFormat("000");
                        if (tipoCompra == 0) { //interna
                            activo.setFactura(dfact.format(f1) + "-" + dfact.format(f2) + "-" + f3);
                        } else { //externa
                            activo.setFactura("999-999-" + f3);
                        }
                    }
                } else {
                    activo.setProveedor(null);
                }
                //            *************************************incrementar para externo********
                if (esExterno) {
                    // es externo
                    if (externoEntidad.getId() == null) {
                        ejbExternos.create(externoEntidad, getSeguridadbean().getLogueado().getUserid());
                    } else {
                        ejbExternos.edit(externoEntidad, getSeguridadbean().getLogueado().getUserid());
                    }
                    activo.setExterno(externoEntidad);
                }
            }
            if (activo.getPadre() == null) {
                activo.setDepreciable(activo.getGrupo().getDepreciable());
                activo.setVidautil(activo.getGrupo().getVidautil());
                activo.setValorresidual(activo.getValorresidual());
            }
            ejbActivos.edit(activo, getSeguridadbean().getLogueado().getUserid());
            if (archivoImagen != null) {
                if (archivoImagen.getId() == null) {
                    imagen.setModulo("ACTIVOFOTO");
                    imagen.setIdmodulo(activo.getId());
                    ejbimagenes.create(imagen);
                    archivoImagen.setImagen(imagen);
                    ejbArchivo.create(archivoImagen);
                } else {
                    imagen.setModulo("ACTIVOFOTO");
                    imagen.setIdmodulo(activo.getId());
                    ejbimagenes.create(imagen);
                    archivoImagen.setImagen(imagen);
                    ejbArchivo.create(archivoImagen);
                }
            }
        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borrar() {
//        if (!menusBean.getPerfil().getBorrado()) {
//            MensajesErrores.advertencia("No tiene autorización para borrar un registro");
//            return null;
//        }
        try {
            ejbActivos.remove(activo, getSeguridadbean().getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }

        formulario.cancelar();
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

//    public void cambiaCodigo(ValueChangeEvent event) {
//        activo = null;
//        if (event.getComponent() instanceof SelectInputText) {
//            // get the number of displayable records from the component
//            SelectInputText autoComplete
//                    = (SelectInputText) event.getComponent();
//            // get the new value typed by component user.
//            String newWord = (String) event.getNewValue();
//            //        getEmpresasBeans().setComercial("");
//
//            List<Activos> aux = new LinkedList<>();
//            // traer la consulta
//            Map parametros = new HashMap();
//            String where = " o.activo=true ";
//            where += " and  upper(o.codigo) like :codigo";
//            parametros.put("codigo", newWord.toUpperCase() + "%");
//            parametros.put(";where", where);
//            parametros.put(";orden", " o.codigo");
//            int total = ((SelectInputText) event.getComponent()).getRows();
//            // Contadores
//            parametros.put(";inicial", 0);
//            parametros.put(";final", total);
//            try {
//                aux = ejbActivos.encontarParametros(parametros);
//            } catch (ConsultarException ex) {
//                MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
//                Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
//            }
//            listaActivo = new ArrayList();
//            for (Activos e : aux) {
//                SelectItem s = new SelectItem(e, e.getCodigo());
//                listaActivo.add(s);
//            }
//            if (autoComplete.getSelectedItem() != null) {
//                activo = (Activos) autoComplete.getSelectedItem().getValue();
//            } else {
//
//                Activos tmp = null;
//                for (int i = 0, max = listaActivo.size(); i < max; i++) {
//                    SelectItem e = (SelectItem) listaActivo.get(i);
//                    if (e.getLabel().compareToIgnoreCase(newWord) == 0) {
//                        tmp = (Activos) e.getValue();
//                    }
//
//                }
//                if (tmp != null) {
//                    activo = tmp;
//                }
//            }
//
//        }
//    }
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

    public Activos traerCodigo(String codigo) {
        Map parametros = new HashMap();
        String where = " o.activo=true ";
        where += " and  upper(o.codigo)=:codigo";
        parametros.put("codigo", codigo.toUpperCase());
        parametros.put(";where", where);
        parametros.put(";orden", " o.codigo");
        try {
            List<Activos> cl = ejbActivos.encontarParametros(parametros);
            for (Activos c : cl) {
                return c;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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

    public void cambiaGrupo(ValueChangeEvent ve) {
        Grupoactivos valor = (Grupoactivos) ve.getNewValue();
        if (valor != null) {
            if (valor.getValorresidual() != null) {
                activo.setValorresidual(valor.getValorresidual().floatValue());
                activo.setDepreciable(valor.getDepreciable());
                activo.setVidautil(valor.getVidautil());
                getSubgrupoBean().setGrupo(valor);
                subgrupoBean.getComboSubGrupo();
            } else {
                activo.setValorresidual(Float.valueOf("0.00"));
            }
        }
    }

    public SelectItem[] getComboSubGrupo() {
        if (activo == null) {
            return null;
        }
        if (activo.getGrupo() == null) {
            return null;
        }
        try {
//            Map parametros = new HashMap();
            Map parametros = new HashMap();
            parametros.put(";orden", "o.nombre");
            parametros.put(";where", "o.grupo=:grupo");
            parametros.put("grupo", activo.getGrupo());
            List<Subgruposactivos> subGrupos = ejbSubGrupos.encontarParametros(parametros);
            return Combos.getSelectItems(subGrupos, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(SubGruposBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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

    public String multimediaListener(FileEntryEvent e) {
        FileEntry fe = (FileEntry) e.getComponent();
        FileEntryResults results = fe.getResults();
        for (FileEntryResults.FileInfo i : results.getFiles()) {
            try {
                File file = i.getFile();
                if (archivoImagen == null) {
                    archivoImagen = new Archivos();
                } else {
                    if (archivoImagen.getId() == null) {
                        archivoImagen = new Archivos();
                    }
                }
                if (imagen == null) {
                    imagen = new Imagenes();
                } else {
                    if (imagen.getId() == null) {
                        imagen = new Imagenes();
                    }
                }
                archivoImagen.setArchivo(Files.readAllBytes(file.toPath()));
//                archivoImagen.setArchivo(Files.readAllBytes(file.toPath()));
                imagen.setNombre(i.getFileName());
                imagen.setFechaingreso(new Date());
                imagen.setTipo("FOTO");
//                archivoImagen.setTipo(i.getContentType());
            } catch (IOException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ActivosBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
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

//    public void cambiaApellido(ValueChangeEvent event) {
//        setEntidad(null);
////        if (event.getComponent() instanceof SelectInputText) {
//            // get the number of displayable records from the component
//            AutoCompleteEntry autoComplete
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
//            int total = 15;
////            int total = ((SelectInputText) event.getComponent()).getRows();
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
//                // pone la localidad
//                if (entidad.getEmpleados() != null) {
//                    if (entidad.getEmpleados().getCargoactual() != null) {
//                        if (entidad.getEmpleados().getOficina() != null) {
//                            activo.setLocalizacion(entidad.getEmpleados().getOficina());
//                        }
//
//                    }
//                }
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
//                    if (entidad.getEmpleados() != null) {
//                        if (entidad.getEmpleados().getCargoactual() != null) {
//                            if (entidad.getEmpleados().getOficina() != null) {
//                                activo.setLocalizacion(entidad.getEmpleados().getOficina());
//                            }
//
//                        }
//                    }
//                }
//            }
//
////        }
//    }
//
//    public void cambiaExterno(ValueChangeEvent event) {
////        setExternoEntidad(null);
//        if (event.getComponent() instanceof SelectInputText) {
//            // get the number of displayable records from the component
//            SelectInputText autoComplete
//                    = (SelectInputText) event.getComponent();
//            // get the new value typed by component user.
//            String newWord = (String) event.getNewValue();
//            //        getEmpresasBeans().setComercial("");
//
//            List<Externos> aux = new LinkedList<>();
//            // traer la consulta
//            Map parametros = new HashMap();
//            String where = " upper(o.empresa) like :empresa ";
////            where += "  upper(o.empresa) like :empresa";
//            parametros.put("empresa", newWord.toUpperCase() + "%");
//            parametros.put(";where", where);
//            parametros.put(";orden", " o.empresa");
//            int total = ((SelectInputText) event.getComponent()).getRows();
//            // Contadores
//            parametros.put(";inicial", 0);
//            parametros.put(";final", total);
//            try {
//                aux = ejbExternos.encontarParametros(parametros);
//            } catch (ConsultarException ex) {
//                MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
//                Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
//            }
//            setListaExternos(new ArrayList());
//            for (Externos e : aux) {
//                SelectItem s = new SelectItem(e, e.getEmpresa());
//                getListaExternos().add(s);
//            }
//            if (autoComplete.getSelectedItem() != null) {
//                setExternoEntidad((Externos) autoComplete.getSelectedItem().getValue());
//                // pone la localidad
//
//            } else {
//
//                Externos tmp = null;
//                for (int i = 0, max = getListaExternos().size(); i < max; i++) {
//                    SelectItem e = (SelectItem) getListaExternos().get(i);
//                    if (e.getLabel().compareToIgnoreCase(newWord) == 0) {
//                        tmp = (Externos) e.getValue();
//                    }
//
//                }
//                if (tmp != null) {
//                    setExternoEntidad(tmp);
//
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
     * @return the listaExternos
     */
    public List<Externos> getListaExternos() {
        return listaExternos;
    }

    /**
     * @param listaExternos the listaExternos to set
     */
    public void setListaExternos(List<Externos> listaExternos) {
        this.listaExternos = listaExternos;
    }

    public byte[] getCodigoBarrasNuevo() {
        if (activo == null) {
            return null;
        }
        if (activo.getCodigo() == null) {
            return null;
        }
        Barcode barcode = null;

        try {

            barcode = BarcodeFactory.createCode39(activo.getCodigo(), true);

        } catch (Exception ex) {
            MensajesErrores.fatal(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger(ActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        barcode.setDrawingText(false);
        barcode.setBarWidth(2);
        barcode.setBarHeight(60);
        barcode.setResolution(70);
        barcode.createImage(300, 100);
        BufferedImage image = new BufferedImage(300, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) image.getGraphics();
        try {
//            File imgFile = new File("/temp/barcode.png");
            barcode.draw(g, 5, 20);
            image.flush();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
//            BarcodeImageHandler.savePNG(barcode, imgFile);
            baos.close();
            return imageInByte;
        } catch (Exception ex) {
            MensajesErrores.fatal(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger(ActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Write the bar code to PNG file
        return null;
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
     * @return the alta
     */
    public int getAlta() {
        return alta;
    }

    /**
     * @param alta the alta to set
     */
    public void setAlta(int alta) {
        this.alta = alta;
    }
//
//    /**
//     * @return the factura
//     */
//    public Integer getFactura() {
//        return factura;
//    }
//
//    /**
//     * @param factura the factura to set
//     */
//    public void setFactura(Integer factura) {
//        this.factura = factura;
//    }

    /**
     * @return the proyectosBean
     */
    public ProyectosBean getProyectosBean() {
        return proyectosBean;
    }

    /**
     * @param proyectosBean the proyectosBean to set
     */
    public void setProyectosBean(ProyectosBean proyectosBean) {
        this.proyectosBean = proyectosBean;
    }

    public void cambiaApellido(ValueChangeEvent event) {
        if (listaEntidades == null) {
            return;
        }
        entidad = null;
        externoEntidad = null;
        String newWord = (String) event.getNewValue();
        for (Entidades e : getListaEntidades()) {
            if (e.getApellidos().compareToIgnoreCase(newWord) == 0) {
                entidad = e;
            }
        }

    }

    public void entidadChangeEventHandler(TextChangeEvent event) {

        String codigoBuscar = event.getNewValue() != null ? (String) event.getNewValue() : "";
        Map parametros = new HashMap();
        parametros.put(";orden", "o.apellidos");
        String where = "  o.activo=true ";
        where += " and upper(o.apellidos) like :codigo";
        parametros.put("codigo", codigoBuscar.toUpperCase() + "%");
        parametros.put(";inicial", 0);
        parametros.put(";final", 15);
        parametros.put(";where", where);
        try {
            setListaEntidades(ejbEntidades.encontarParametros(parametros));
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }

    }

    /**
     * @return the listaEntidades
     */
    public List<Entidades> getListaEntidades() {
        return listaEntidades;
    }

    /**
     * @param listaEntidades the listaEntidades to set
     */
    public void setListaEntidades(List<Entidades> listaEntidades) {
        this.listaEntidades = listaEntidades;
    }

    public void cambiaExterno(ValueChangeEvent event) {
        if (listaExternos == null) {
            return;
        }
        externoEntidad = null;
        externo = null;
        String newWord = (String) event.getNewValue();
        for (Externos e : listaExternos) {
            if (e.getEmpresa().compareToIgnoreCase(newWord) == 0) {
                externoEntidad = e;
                return;
            }
        }
    }

    public void externoChangeEventHandler(TextChangeEvent event) {

        // get the new value typed by component user.
        String newWord = (String) event.getNewValue();
        Map parametros = new HashMap();
        String where = " upper(o.empresa) like :empresa ";
        parametros.put("empresa", newWord.toUpperCase() + "%");
        parametros.put(";where", where);
        parametros.put(";orden", " o.empresa");
        int total = 15;
        // Contadores
        parametros.put(";inicial", 0);
        parametros.put(";final", total);
        try {
            listaExternos = ejbExternos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }
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

    public String reporte(Activos ac) {
        activo = ac;
        if (activo.getPadre() != null) {
            return null;
        }
        try {
            //Ver si se encuentra en acta
            Map parametros = new HashMap();
            parametros.put(";where", "o.activo=:activo and o.acta.tipo.codigo='06'");
            parametros.put("activo", activo);
            List<Actasactivos> listaAA = ejbActasactivos.encontarParametros(parametros);
            if (listaAA.isEmpty()) {
                MensajesErrores.advertencia("El activo no tiene acta de Ingreso");
            } else {
                Actasactivos aa = listaAA.get(0);
                actasActivosBean.imprime(aa.getActa());
            }
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage());
            Logger.getLogger(ActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public double getValorIva() {
        valor = 0.00;
        if (activo.getValorAltaSinIva() != null) {
            activo.setValorreposicion(activo.getValoralta());
            if (activo.getIva() != null && activo.getIva().doubleValue() != 0.00) {
                valor = activo.getValorAltaSinIva().doubleValue() * activo.getIva().doubleValue();
            } else {
                valor = 0.00;
            }
        }
        return valor;
    }

    public double getValorTotal() {
        valor = 0.00;
        if (activo.getValorAltaSinIva() != null) {
            if (activo.getIva() != null && activo.getIva().doubleValue() != 0.00) {
                valor = (activo.getValorAltaSinIva().doubleValue()) + (activo.getValorAltaSinIva().doubleValue() * activo.getIva().doubleValue());
            } else {
                valor = activo.getValorAltaSinIva().doubleValue();
            }
        }
        activo.setValoralta(new BigDecimal(valor));
        return valor;
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

            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "ACTA INGRESO"));
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
                    campos.add(new AuxiliarReporte("String", e.getCustodio() != null ? (e.getCustodio().getEntidad().getPin()) : (e.getExterno() != null ? e.getExterno().getEmpresa() : "")));
                    campos.add(new AuxiliarReporte("String", e.getCustodio() != null ? (e.getCustodio().getEntidad().toString()) : (e.getExterno() != null ? e.getExterno().getNombre() : "")));
                    campos.add(new AuxiliarReporte("String", e.getLocalizacion() != null ? (e.getLocalizacion().getEdificio() + "-" + e.getLocalizacion().getNombre()) : ""));
                    int actaUlt;
                    if (e.getActaultima() == null) {
                        actaUlt = 0;
                    } else {
                        actaUlt = e.getActaultima();
                    }
                    String fechaUltima = traerFecha(e, actaUlt);
                    String tipoActa = traerTipoActa(e, actaUlt);
                    campos.add(new AuxiliarReporte("String", e.getActa() != null ? (e.getActa() + " - Ingreso de Bienes") + "" : ""));
//                    campos.add(new AuxiliarReporte("String", "")); //"EJ/PER.CTBLE"
                    campos.add(new AuxiliarReporte("String", e.getActaultima() != null ? (e.getActaultima() + " - " + tipoActa) : ""));
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
            setReporte(xls.traerRecurso());
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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            List<Actasactivos> lista = ejbActasactivos.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                for (Actasactivos aa : lista) {
                    fecha = aa.getActa().getFecha();
                }
                if (fecha != null) {
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

    public String traerTipoActa(Activos act, int numero) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.activo=:activo and o.acta.numerotexto=:numerotexto");
        parametros.put("activo", act);
        parametros.put("numerotexto", numero + "");
        try {
            String num = null;
            List<Actasactivos> lista = ejbActasactivos.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                for (Actasactivos aa : lista) {
                    num = aa.getActa().getTipo().getNombre();
                }
                if (num != null) {
                    return num;
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

    public String traerTipoEstado(Activos act) {
        if (act.getFechaingreso() != null) {
            if (act.getFechaingreso() != null && act.getCustodio() == null && act.getExterno() == null) {
                if (act.getFechaingreso() != null && act.getCustodio() == null && act.getExterno() == null && act.getBaja() != null && act.getFechasolicitud() != null) {
                    if (act.getFechaingreso() != null && act.getCustodio() == null && act.getExterno() == null && act.getBaja() != null && act.getFechasolicitud() != null && act.getFechabaja() != null) {
                        return "Dado de Baja";
                    }
                    return "Inicio de Baja";
                }
            }else{
                if (act.getFechaingreso() != null && (act.getCustodio() != null || act.getExterno() != null)) {
                    return "Asignado";
                }
            }
            return "Ingresado";
        }
        return "";
    }

    /**
     * @return the listaActivosReporte
     */
    public List<Activos> getListaActivosReporte() {
        return listaActivosReporte;
    }

    /**
     * @param listaActivosReporte the listaActivosReporte to set
     */
    public void setListaActivosReporte(List<Activos> listaActivosReporte) {
        this.listaActivosReporte = listaActivosReporte;
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
     * @return the tipoActivo
     */
    public int getTipoActivo() {
        return tipoActivo;
    }

    /**
     * @param tipoActivo the tipoActivo to set
     */
    public void setTipoActivo(int tipoActivo) {
        this.tipoActivo = tipoActivo;
    }

    /**
     * @return the responsable
     */
    public Entidades getResponsable() {
        return responsable;
    }

    /**
     * @param responsable the responsable to set
     */
    public void setResponsable(Entidades responsable) {
        this.responsable = responsable;
    }

    /**
     * @return the tipoAuxiliar
     */
    public String getTipoAuxiliar() {
        return tipoAuxiliar;
    }

    /**
     * @param tipoAuxiliar the tipoAuxiliar to set
     */
    public void setTipoAuxiliar(String tipoAuxiliar) {
        this.tipoAuxiliar = tipoAuxiliar;
    }

    /**
     * @return the nominativo
     */
    public String getNominativo() {
        return nominativo;
    }

    /**
     * @param nominativo the nominativo to set
     */
    public void setNominativo(String nominativo) {
        this.nominativo = nominativo;
    }

    /**
     * @return the valor
     */
    public double getValor() {
        return valor;
    }

    /**
     * @param valor the valor to set
     */
    public void setValor(double valor) {
        this.valor = valor;
    }

    /**
     * @return the nombrepdf
     */
    public String getNombrepdf() {
        return nombrepdf;
    }

    /**
     * @param nombrepdf the nombrepdf to set
     */
    public void setNombrepdf(String nombrepdf) {
        this.nombrepdf = nombrepdf;
    }

    /**
     * @return the padre
     */
    public Activos getPadre() {
        return padre;
    }

    /**
     * @param padre the padre to set
     */
    public void setPadre(Activos padre) {
        this.padre = padre;
    }

    /**
     * @return the contrato
     */
    public Contratos getContrato() {
        return contrato;
    }

    /**
     * @param contrato the contrato to set
     */
    public void setContrato(Contratos contrato) {
        this.contrato = contrato;
    }

    /**
     * @return the factura
     */
    public String getFactura() {
        return factura;
    }

    /**
     * @param factura the factura to set
     */
    public void setFactura(String factura) {
        this.factura = factura;
    }

    /**
     * @return the f1
     */
    public int getF1() {
        return f1;
    }

    /**
     * @param f1 the f1 to set
     */
    public void setF1(int f1) {
        this.f1 = f1;
    }

    /**
     * @return the f2
     */
    public int getF2() {
        return f2;
    }

    /**
     * @param f2 the f2 to set
     */
    public void setF2(int f2) {
        this.f2 = f2;
    }

    /**
     * @return the f3
     */
    public int getF3() {
        return f3;
    }

    /**
     * @param f3 the f3 to set
     */
    public void setF3(int f3) {
        this.f3 = f3;
    }

    /**
     * @return the fechaIngreso
     */
    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    /**
     * @param fechaIngreso the fechaIngreso to set
     */
    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
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
     * @return the tipoCompra
     */
    public int getTipoCompra() {
        return tipoCompra;
    }

    /**
     * @param tipoCompra the tipoCompra to set
     */
    public void setTipoCompra(int tipoCompra) {
        this.tipoCompra = tipoCompra;
    }

    /**
     * @return the actasActivosBean
     */
    public ActasActivosBean getActasActivosBean() {
        return actasActivosBean;
    }

    /**
     * @param actasActivosBean the actasActivosBean to set
     */
    public void setActasActivosBean(ActasActivosBean actasActivosBean) {
        this.actasActivosBean = actasActivosBean;
    }
}
