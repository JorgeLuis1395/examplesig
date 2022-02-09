/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.activos.sfccbdmq;

//import com.icesoft.faces.component.selectinputtext.SelectInputText;
import com.ejb.sfcarchivos.ArchivosFacade;
import com.ejb.sfcarchivos.ImagenesFacade;
import com.entidades.sfcarchivos.Archivos;
import com.entidades.sfcarchivos.Imagenes;
import com.lowagie.text.DocumentException;
import org.compras.sfccbdmq.ProveedoresBean;
import org.presupuestos.sfccbdmq.ClasificadorBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.File;
import java.io.IOException;
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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Constantes;
import org.auxiliares.sfccbdmq.DocumentoPDF;
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
import org.utilitarios.sfccbdmq.Recurso;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "activosControlSfccbdmq")
@ViewScoped
public class ActivosControlBean {

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

    /**
     * Creates a new instance of ActivoBean
     */
    public ActivosControlBean() {
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
    private boolean esExterno = false;
    private Externos externoEntidad = new Externos();
    //
    private LazyDataModel<Activos> activos;
    private List<Activoobligacion> obligaciones;
    private List<Activos> hijos;
    private List<Trackingactivos> tackings;
    private List<Activos> listaActivosReporte;
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
    private Codigos estado;
    private Codigos institucion;
    private Oficinas oficina;
    private Edificios edificio;
    private Polizas poliza;
    private List listaActivo;
    private Integer factura;
    private int garantia;
    private int operativo;
    private String responsable;
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
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    private Perfiles perfil;
    private List<Entidades> listaEntidades;
    private List<Externos> listaExternos;
    private Recurso reportepdf;
    private DocumentoPDF pdf;
    private Formulario formularioImprimir = new Formulario();

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
        String nombreForma = "RegistroControlActivosVista";
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
                    parametros.put(";orden", "o.codigo");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }
                String where = "  o.baja is null and o.control=true ";
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }
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
                if (oficinasBean.getEdificio() != null) {
                    if (oficina != null) {
                        where += " and o.localizacion=:localizacion";
                        parametros.put("localizacion", oficina);
                    } else {
                        where += " and o.localizacion.edificio=:edificio";
                        parametros.put("edificio", oficinasBean.getEdificio());
                    }
                }
                if (factura != null) {
                    where += " and o.factura=:factura";
                    parametros.put("factura", factura);
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
                return null;
            }
        };
        return null;
    }

    public String nuevo() {
        activo = new Activos();
        formulario.insertar();
        archivoImagen = null;
        imagen = null;
        entidad = null;
        apellidos = null;
        listaUsuarios = null;
        activo.setControl(true);
        esExterno = false;
        externoEntidad = new Externos();
        return null;
    }

    public String nuevoComponente() {
        activo = new Activos();
        Activos padre = (Activos) activos.getRowData();
        activo.setPadre(padre);
        activo.setGrupo(padre.getGrupo());
        activo.setClasificacion(padre.getClasificacion());
        activo.setVidautil(padre.getVidautil());
        activo.setValorresidual(padre.getValorresidual());
        formulario.insertar();
        archivoImagen = null;
        imagen = null;
        entidad = null;
        apellidos = null;
        listaUsuarios = null;
        esExterno = false;
        activo.setControl(true);
        externoEntidad = new Externos();
        subgrupoBean.setGrupo(grupo);
        subgrupoBean.getComboSubGrupo();
        return null;
    }

    public String modifica() {
//        if (!menusBean.getPerfil().getModificacion()) {
//            MensajesErrores.advertencia("No tiene autorización para modificar un registro");
//            return null;
//        }
        activo = (Activos) activos.getRowData();
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
            listadoErogaciones = ejbErogaciones.encontarParametros(parametros);
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

    public String elimina() {
//        if (!menusBean.getPerfil().getBorrado()) {
//            MensajesErrores.advertencia("No tiene autorización para borrar un registro");
//            return null;
//        }

        activo = (Activos) activos.getRowData();
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
            listadoErogaciones = ejbErogaciones.encontarParametros(parametros);
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
        formulario.eliminar();
        return null;
    }

    private boolean validar() {
//        if ((activo.getCodigo() == null) || (activo.getCodigo().isEmpty())) {
//            MensajesErrores.advertencia("Código es Obligatorio es obligatorio");
//            return true;
//        }
        if ((activo.getDescripcion() == null) || (activo.getDescripcion().isEmpty())) {
            MensajesErrores.advertencia("Descripcion es obligatoria");
            return true;
        }

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
//        validacccion de custorio retirado
//        if (esExterno) {
//            if ((externoEntidad.getDireccion() == null) || (externoEntidad.getDireccion().isEmpty())) {
//                MensajesErrores.advertencia("Dirección es obligatorio");
//                return true;
//            }
//            if ((externoEntidad.getEmpresa() == null) || (externoEntidad.getEmpresa().isEmpty())) {
//                MensajesErrores.advertencia("Empresa es obligatoria");
//                return true;
//            }
//            if ((externoEntidad.getNombre() == null) || (externoEntidad.getNombre().isEmpty())) {
//                MensajesErrores.advertencia("Nombre es obligatorio");
//                return true;
//            }
//            if ((externoEntidad.getTelefonos() == null) || (externoEntidad.getTelefonos().isEmpty())) {
//                MensajesErrores.advertencia("Teléfonos son obligatorios");
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

        return false;
    }

    public String insertar() {
//        if (!menusBean.getPerfil().getNuevo()) {
//            MensajesErrores.advertencia("No tiene autorización para crear un registro");
//            return null;
//        }

        if (validar()) {
            return null;
        }

        try {
            Integer numero = activo.getGrupo().getSecuencia();
            numero++;
            Trackingactivos t = new Trackingactivos();
//            DecimalFormat df = new DecimalFormat("0000000000");
//            activo.setCodigo(activo.getGrupo().getTipo().getCodigo() + df.format(numero));
            DecimalFormat df = new DecimalFormat("00000");

//            activo.setCodigo(activo.getGrupo().getCodigo()+activo.getSubgrupo().getCodigo() + df.format(numero));
            if (activo.getPadre() == null) {
                activo.setDepreciable(activo.getGrupo().getDepreciable());
                activo.setVidautil(activo.getGrupo().getVidautil());
                activo.setValorresidual(activo.getValorresidual());
            }
            activo.setFechaingreso(new Date());
            ejbActivos.create(activo, getSeguridadbean().getLogueado().getUserid());
            activo.setCodigo(activo.getSubgrupo().getCodigo() + df.format(activo.getId()));
            ejbActivos.edit(activo, getSeguridadbean().getLogueado().getUserid());
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
            t.setCuenta1("");
            t.setCuenta2("");
            t.setUsuario(getSeguridadbean().getLogueado().getUserid());
            t.setValor(activo.getValoralta() == null ? 0 : activo.getValoralta().floatValue());
            t.setValornuevo(activo.getValoradquisiscion() == null ? 0 : activo.getValoradquisiscion().floatValue());
            ejbTracking.create(t, getSeguridadbean().getLogueado().getUserid());
            //Número de acta
            Codigos tipoActa = codigosBean.traerCodigo(Constantes.ACTAS, "06");
            if (tipoActa == null) {
                MensajesErrores.advertencia("No existe tipo de acta de código 06");
                return null;
            }Codigos configuracion = codigosBean.traerCodigo(Constantes.CONFIGURACION_ACTAS, "06");
            if (configuracion == null) {
                MensajesErrores.advertencia("No existe configuración de acta de código 06");
                return null;
            }
            Actas actaNueva = new Actas();
            actaNueva.setAceptacion(configuracion.getNombre());
            actaNueva.setAntecedentes(configuracion.getDescripcion());
            actaNueva.setTipo(tipoActa);
            String numeroActa = tipoActa.getParametros();
            if ((numeroActa == null) || (numeroActa.isEmpty())) {
                numeroActa = "1";
            }
            int num = Integer.parseInt(numeroActa);
            num++;
            Date fecha = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            DecimalFormat df1 = new DecimalFormat("000000");
            actaNueva.setNumero(num);
            actaNueva.setNumerotexto(sdf.format(fecha) + df1.format(num));
            actaNueva.setReciben("@" + "BODEGA@");
            tipoActa.setParametros("" + num);
            ejbCodigos.edit(tipoActa, seguridadbean.getLogueado().getUserid());
            ejbActas.create(actaNueva, seguridadbean.getLogueado().getUserid());
            Actasactivos aa = new Actasactivos();
            aa.setActa(actaNueva);
            aa.setActivo(activo);

            Grupoactivos g = activo.getGrupo();
            g.setSecuencia(numero);
            ejbGrupos.edit(g, getSeguridadbean().getLogueado().getUserid());
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        reporte(activo);
        return null;
    }

    public String grabar() {

        try {

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
        } catch (GrabarException ex) {
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
            // primero componentes
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
            Logger.getLogger(ActivosControlBean.class.getName()).log(Level.SEVERE, null, ex);
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
            activo.setValorresidual(valor.getValorresidual().floatValue());
            activo.setDepreciable(valor.getDepreciable());
            activo.setVidautil(valor.getVidautil());
            getSubgrupoBean().setGrupo(valor);
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
                archivoImagen = new Archivos();
                imagen = new Imagenes();
                archivoImagen.setArchivo(Files.readAllBytes(file.toPath()));
                imagen.setNombre(i.getFileName());
                imagen.setFechaingreso(new Date());
//                archivoImagen.setTipo(i.getContentType());
            } catch (IOException ex) {
                MensajesErrores.fatal(ex.getMessage() + ":" + ex.getCause());
                Logger.getLogger(BajasActivosBean.class.getName()).log(Level.SEVERE, null, ex);
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

    public void cambiaExterno(ValueChangeEvent event) {
        if (listaExternos == null) {
            return;
        }
        externoEntidad = null;
        externo = null;
        String newWord = (String) event.getNewValue();
        for (Externos e : getListaExternos()) {
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
            setListaExternos(ejbExternos.encontarParametros(parametros));
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
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

    public String reporte(Activos activo) {
        this.activo = activo;
        listaActivosReporte = new LinkedList<>();
        try {
            //Ver si esta en la carga masiva
            Map parametros = new HashMap();
            parametros.put(";where", "o.activo=:activo and o.acta.tipo.codigo='06'");
            parametros.put("activo", activo);
            List<Actasactivos> listaAA = ejbActasactivos.encontarParametros(parametros);
            if (!listaAA.isEmpty()) {
                Actasactivos aa = listaAA.get(0);
                responsable = aa.getActa().getReciben();
                parametros = new HashMap();
                parametros.put(";where", "o.acta=:acta");
                parametros.put("acta", aa.getActa());
                List<Actasactivos> listaReporte = ejbActasactivos.encontarParametros(parametros);
                if (!listaReporte.isEmpty()) {
                    for (Actasactivos aA : listaReporte) {
                        parametros = new HashMap();
                        String numeroS = aA.getActivo().toString();
                        Integer num = Integer.parseInt(numeroS.trim());
                        parametros.put(";where", "o.id=:id");
                        parametros.put("id", num);
                        List<Activos> listaActivosActivo = ejbActivos.encontarParametros(parametros);
                        if (!listaActivosActivo.isEmpty()) {
                            Activos a = listaActivosActivo.get(0);
                            listaActivosReporte.add(a);
                        }
                    }
                    reporteMasivo(listaActivosReporte);
                    return null;
                }
            }

            parametros = new HashMap();
            parametros.put(";where", "o.id=:id");
            parametros.put("id", activo.getId());

            List<Activos> listaActivos = ejbActivos.encontarParametros(parametros);
            pdf = new DocumentoPDF("EMPRESA PUBLICA METROPOLITANA DE LOGISTICA PARA LA SEGURIDAD", null, seguridadbean.getLogueado().getUserid());
            pdf.agregaParrafoCompleto("\n",AuxiliarReporte.ALIGN_CENTER, 6, false);
            pdf.agregaParrafoCompleto("ACTIVOS FIJOS",AuxiliarReporte.ALIGN_CENTER, 6, false);
            pdf.agregaParrafoCompleto("\n",AuxiliarReporte.ALIGN_CENTER, 6, false);
            pdf.agregaParrafoCompleto("INGRESO DE BIENES",AuxiliarReporte.ALIGN_CENTER, 6, false);
            pdf.agregaParrafoCompleto("\n\n\n\n",AuxiliarReporte.ALIGN_CENTER, 6, false);
            pdf.agregaParrafoCompleto("DETALLE DE LOS BIENES",AuxiliarReporte.ALIGN_CENTER, 6, false);
            pdf.agregaParrafoCompleto("\n",AuxiliarReporte.ALIGN_CENTER, 6, false);

            List<AuxiliarReporte> valores = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, true, "CODIGO" + "\n" + "ACTIVO"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, true, "DESCRIPCION"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, true, "MARCA"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, true, "MODELO"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, true, "ESTADO DEL BIEN"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, true, "SERIE"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 8, true, "VALOR"));

            for (Activos a : listaActivos) {

                valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, a.getCodigo()));
                valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, a.getDescripcion()));
//                valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, a.getMarca()));
                valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, a.getActivomarca() != null ? a.getActivomarca().getNombre() : a.getMarca()));
                valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, a.getModelo()));
                valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, a.getEstado().getNombre()));
                valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, a.getNumeroserie()));
                valores.add(new AuxiliarReporte("Double", 9, AuxiliarReporte.ALIGN_RIGHT, 8, false, a.getValoralta().doubleValue()));
            }

            pdf.agregarTablaReporte(titulos, valores, 7, 100, "");

            pdf.agregaParrafo("\n");
            double iva = 0.00;
            valores = new LinkedList<>();
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, "Subtotal: "));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_RIGHT, 6, false, activo.getValoralta().doubleValue()));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, "IVA: "));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_RIGHT, 6, false, activo.getIva() == null ? iva : (activo.getIva().equals(0.00)) ? iva : (activo.getValoralta().doubleValue() * activo.getIva().doubleValue())));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, "Total: "));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, listaActivos.size() + ""));
            valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_RIGHT, 6, false, activo.getIva() == null ? (activo.getValoralta().doubleValue()) : (activo.getIva().equals(0.00)) ? (activo.getValoralta().doubleValue()) : (activo.getValoralta().doubleValue() + (activo.getValoralta().doubleValue() * activo.getIva().doubleValue()))));
            pdf.agregarTabla(null, valores, 3, 100, "");

            pdf.agregaParrafo("\n");
            pdf.agregaParrafo("OBSERVACIÓN :");
            pdf.agregaParrafo("Esta entrega recepción se sujeta a las siguientes cláusulas conforme establece el Reglamento"
                    + "General Sustitutivo pare el Manejo y Administración de bienes del Sector Público:\n"
                    + "• Todos los bienes descritos en la presente Acta serán de exclusiva responsabilidad, buen uso y cuidado de quien(es) reciben.\n"
                    + "• En caso de cambio, retiro o incremento de bienes, estos deberán ser notificados al la Unidad de Gestión de Bienes, para su actualización, registro y control.\n"
                    + "• En caso de perdida, deberá notificar a través de su Jefe Inmediato o a la Máxima Autoridad.\n "
                    + "• En caso de establecer responsabilidad en su contra, la reposición de los bienes, se hará en dinero a precio del mercado o en especies de iguales "
                    + "caracteristicas del bien desaparecido, destruido o inutilizado.\n");
            pdf.agregaParrafo("\n");
            Empleados e = null;
            parametros = new HashMap();
            parametros.put(";where", "o.cargoactual.descripcion='RESPONSABLE DE UNIDAD DE BIENES'");
            List<Empleados> lista = ejbEmpleados.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                e = lista.get(0);
            }
            valores = new LinkedList<>();
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, "ENTREGUE CONFORME"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, "UNIDADES DE BIENES"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, "ELABORADO POR"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, "\n\n\n\n\n"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, "\n\n\n\n\n"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, "\n\n\n\n\n"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 8, false, "___________________________________________"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 8, false, "___________________________________________"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 8, false, "___________________________________________"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 11, false, "ADMINISTRADOR DEL CONTRATO"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 11, false, "RESPONSABLE DE LA UNIDAD DE BIENES\n" + (e != null ? e.toString() : "")));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 11, false, seguridadbean.getLogueado().toString()));
            pdf.agregarTabla(null, valores, 3, 100, "");
            
            reportepdf = pdf.traerRecurso();
        } catch (IOException | DocumentException | ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger(ActivosControlBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioImprimir.insertar();
        return null;
    }

    public String reporteMasivo(List<Activos> activos) {
        listaActivosReporte = activos;
        try {
            pdf = new DocumentoPDF("EMPRESA PUBLICA METROPOLITANA DE LOGISTICA PARA LA SEGURIDAD", null, seguridadbean.getLogueado().getUserid());
            pdf.agregaParrafoCompleto("\n",AuxiliarReporte.ALIGN_CENTER, 6, false);
            pdf.agregaParrafoCompleto("ACTIVOS FIJOS",AuxiliarReporte.ALIGN_CENTER, 6, false);
            pdf.agregaParrafoCompleto("\n",AuxiliarReporte.ALIGN_CENTER, 6, false);
            pdf.agregaParrafoCompleto("INGRESO DE BIENES",AuxiliarReporte.ALIGN_CENTER, 6, false);
            pdf.agregaParrafoCompleto("\n\n\n\n",AuxiliarReporte.ALIGN_CENTER, 6, false);
            pdf.agregaParrafoCompleto("DETALLE DE LOS BIENES",AuxiliarReporte.ALIGN_CENTER, 6, false);
            pdf.agregaParrafoCompleto("\n",AuxiliarReporte.ALIGN_CENTER, 6, false);

            List<AuxiliarReporte> valores = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, true, "CODIGO" + "\n" + "ACTIVOS"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, true, "DESCRIPCION"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, true, "MARCA"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, true, "MODELO"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, true, "ESTADO DEL BIEN"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, true, "SERIE"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 8, true, "VALOR"));
            //Activos Fijos
            Double SubtotalAF = 0.00;
            Double IvaAF = 0.00;
            Double totalIvaAF = 0.00;
            Double totalSinIvaAF = 0.00;
            Double totalAF = 0.00;
            Integer numeroAF = 0;
            for (Activos a : listaActivosReporte) {
                if (a.getControl()) {
                    valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, a.getCodigo() != null ? a.getCodigo() : ""));
                    valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, a.getDescripcion() != null ? a.getDescripcion() : ""));
                    valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, a.getMarca() != null ? a.getMarca() : ""));
                    valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, a.getModelo() != null ? a.getModelo() : ""));
                    valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, a.getEstado() != null ? a.getEstado().getNombre() : ""));
                    valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, a.getNumeroserie() != null ? a.getNumeroserie() : ""));
                    valores.add(new AuxiliarReporte("Double", 9, AuxiliarReporte.ALIGN_RIGHT, 8, false, a.getValoralta() != null ? a.getValoralta().doubleValue() : ""));
                    SubtotalAF += a.getValoralta().doubleValue();
                    numeroAF++;
                    if (!(a.getIva() == null && a.getId().equals(0.00))) {
                        IvaAF += a.getValoralta().doubleValue() * a.getIva().doubleValue();
                        totalIvaAF += a.getValoralta().doubleValue() + (a.getValoralta().doubleValue() * a.getIva().doubleValue());
                    } else {
                        totalSinIvaAF += a.getValoralta().doubleValue();
                    }
                }
            }
            totalAF = totalIvaAF + totalSinIvaAF;
            pdf.agregarTablaReporte(titulos, valores, 7, 100, "Activos Fijos");
            pdf.agregaParrafo("\n");
            if (numeroAF > 1) {
                valores = new LinkedList<>();
                valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, "Total Activos Fijos: "));
                valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, numeroAF + ""));
                valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_RIGHT, 6, false, SubtotalAF));
                pdf.agregarTabla(null, valores, 3, 100, "");
                pdf.agregaParrafo("\n");
                pdf.agregaParrafo("\n");
            }
            //Activos de Control
            valores = new LinkedList<>();
            Double SubtotalAC = 0.00;
            Double IvaAC = 0.00;
            Double totalIvaAC = 0.00;
            Double totalSinIvaAC = 0.00;
            Double totalAC = 0.00;
            Integer numeroAC = 0;
            for (Activos a : listaActivosReporte) {
                if (!a.getControl()) {
                    valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, a.getCodigo() != null ? a.getCodigo() : ""));
                    valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, a.getDescripcion() != null ? a.getDescripcion() : ""));
                    valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, a.getMarca() != null ? a.getMarca() : ""));
                    valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, a.getModelo() != null ? a.getModelo() : ""));
                    valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, a.getEstado() != null ? a.getEstado().getNombre() : ""));
                    valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, a.getNumeroserie() != null ? a.getNumeroserie() : ""));
                    valores.add(new AuxiliarReporte("Double", 9, AuxiliarReporte.ALIGN_RIGHT, 8, false, a.getValoralta() != null ? a.getValoralta().doubleValue() : ""));
                    SubtotalAC += a.getValoralta().doubleValue();
                    numeroAC++;
                    if (!(a.getIva() == null && a.getId().equals(0.00))) {
                        IvaAC += a.getValoralta().doubleValue() * a.getIva().doubleValue();
                        totalIvaAC += a.getValoralta().doubleValue() + (a.getValoralta().doubleValue() * a.getIva().doubleValue());
                    } else {
                        totalSinIvaAC += a.getValoralta().doubleValue();
                    }
                }
            }
            totalAC = totalIvaAC + totalSinIvaAC;
            pdf.agregarTablaReporte(titulos, valores, 7, 100, "Bienes de Control");
            pdf.agregaParrafo("\n");
            if (numeroAC > 1) {
                valores = new LinkedList<>();
                valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, "Total Activos de Control: "));
                valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, numeroAC + ""));
                valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_RIGHT, 6, false, SubtotalAC));
                pdf.agregarTabla(null, valores, 3, 100, "");
                pdf.agregaParrafo("\n");
                pdf.agregaParrafo("\n");
            }

            valores = new LinkedList<>();
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, "Subtotal: "));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_RIGHT, 6, false, SubtotalAF + SubtotalAC));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, "IVA: "));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_RIGHT, 6, false, IvaAF + IvaAC));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, "Total: "));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, activos.size() + ""));
            valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_RIGHT, 6, false, totalAF + totalAC));
            pdf.agregarTabla(null, valores, 3, 100, "");
            pdf.agregaParrafo("\n");
            pdf.agregaParrafo("OBSERVACIÓN :");
            pdf.agregaParrafo("Esta entrega recepción se sujeta a las siguientes cláusulas conforme establece el Reglamento"
                    + "General Sustitutivo pare el Manejo y Administración de bienes del Sector Público:\n"
                    + "• Todos los bienes descritos en la presente Acta serán de exclusiva responsabilidad, buen uso y cuidado de quien(es) reciben.\n"
                    + "• En caso de cambio, retiro o incremento de bienes, estos deberán ser notificados al la Unidad de Gestión de Bienes, para su actualización, registro y control.\n"
                    + "• En caso de perdida, deberá notificar a través de su Jefe Inmediato o a la Máxima Autoridad.\n "
                    + "• En caso de establecer responsabilidad en su contra, la reposición de los bienes, se hará en dinero a precio del mercado o en especies de iguales "
                    + "caracteristicas del bien desaparecido, destruido o inutilizado.\n");
            pdf.agregaParrafo("\n");
            Empleados e = null;
            Map parametros = new HashMap();
            parametros.put(";where", "o.cargoactual.descripcion='RESPONSABLE DE UNIDAD DE BIENES'");
            List<Empleados> lista = ejbEmpleados.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                e = lista.get(0);
            }
            valores = new LinkedList<>();
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, "ENTREGUE CONFORME"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, "UNIDADES DE BIENES"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, "ELABORADO POR"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, "\n\n\n\n\n"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, "\n\n\n\n\n"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, "\n\n\n\n\n"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 8, false, "___________________________________________"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 8, false, "___________________________________________"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 8, false, "___________________________________________"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 11, false, "ADMINISTRADOR DEL CONTRATO"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 11, false, "RESPONSABLE DE LA UNIDAD DE BIENES\n" + (e != null ? e.toString() : "")));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 11, false, responsable));
            pdf.agregarTabla(null, valores, 3, 100, "");
            
            reportepdf = pdf.traerRecurso();
        } catch (IOException | DocumentException | ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger(CargarActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioImprimir.insertar();
        return null;
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
     * @return the responsable
     */
    public String getResponsable() {
        return responsable;
    }

    /**
     * @param responsable the responsable to set
     */
    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }
}
