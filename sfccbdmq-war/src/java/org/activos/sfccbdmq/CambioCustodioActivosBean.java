/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.activos.sfccbdmq;

//import com.icesoft.faces.component.selectinputtext.SelectInputText;
import com.ejb.sfcarchivos.ArchivosFacade;
import com.entidades.sfcarchivos.Archivos;
import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import org.compras.sfccbdmq.ProveedoresBean;
import org.presupuestos.sfccbdmq.ClasificadorBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
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
import org.entidades.sfccbdmq.Tipoactivo;
import org.entidades.sfccbdmq.Trackingactivos;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.icefaces.ace.event.TextChangeEvent;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.CodigosBean;
import org.utilitarios.sfccbdmq.Recurso;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "cambioCustodioActivosSfccbdmq")
@ViewScoped
public class CambioCustodioActivosBean {

    /**
     * Creates a new instance of ActivoBean
     */
    public CambioCustodioActivosBean() {
        activos = new LazyDataModel<Activos>() {

            @Override
            public List<Activos> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return new LinkedList<>();
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
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private ActasactivosFacade ejbActasactivos;
    @EJB
    private ActasFacade ejbActas;
    @EJB
    private CodigosFacade ejbCodigos;
    private Archivos archivoImagen;
//    private int tamano;
    private String codigoAnterior;
    private Formatos nivel;
    private Activos activo;
    private Formulario formulario = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Formulario formularioImprimi = new Formulario();
    private Formulario formularioExterno = new Formulario();
    private LazyDataModel<Activos> activos;
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
    private Oficinas oficina;
    private Edificios edificio;
    private Polizas poliza;
    private Integer factura;
    private Externos externoEntidad = new Externos();
    private Entidades entidad;
    private String apellidos;
    private List listaUsuarios;
    private List<Entidades> listaEntidades;
    private List<Externos> listaExternos;
    private boolean esExterno = false;
    private int garantia;
    private int operativo;
    private int control;
    private String tipoAuxiliar;
    private String nominativo;
    private Recurso reportepdf;
    private DocumentoPDF pdf;
    private String nombrepdf;
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
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;

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
        String nombreForma = "CambioCustodioActivosVista";
        if (perfil == null) {
            MensajesErrores.fatal("Sin perfil v??lido");
            seguridadbean.cerraSession();
        }
        this.setPerfil(seguridadbean.traerPerfil(perfil));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil v??lido");
            seguridadbean.cerraSession();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(seguridadbean.getGrpUsuario().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil v??lido, grupo invalido");
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
                //                ES- COMENTADO PARA PRUEBAS 
                String where = "  o.baja is null and o.padre is null and o.fechaalta is not null ";
//                String where = "  o.baja is null and o.padre is null ";
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
                if (factura != null) {
                    where += " and o.factura=:factura";
                    parametros.put("factura", factura);
                }
                if (!((tipoAuxiliar == null) || (tipoAuxiliar.isEmpty()))) {
                    where += " and o.tipoauxiliar=:tipoauxiliar";
                    parametros.put("tipoauxiliar", tipoAuxiliar);
                }
                if (!((nominativo == null) || (nominativo.isEmpty()))) {
                    where += " and o.nominativo=:nominativo";
                    parametros.put("nominativo", nominativo);
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

    public String modifica() {
        activo = (Activos) activos.getRowData();
        entidad = null;
        apellidos = null;
        imagenesBean.setIdModulo(activo.getId());
        imagenesBean.setModulo("ACTIVO");
        imagenesBean.Buscar();
        Map parametros = new HashMap();
        parametros.put(";where", "o.padre=:padre and o.baja is null");
        parametros.put("padre", activo);
        try {
            hijos = ejbActivos.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put(";where", "o.activo=:padre");
            parametros.put("padre", activo);
            tackings = ejbTracking.encontarParametros(parametros);
            obligaciones = ejbObligacion.encontarParametros(parametros);
            depreciaciones = ejbDepreciaciones.encontarParametros(parametros);
            listadoErogaciones = ejbErogaciones.encontarParametros(parametros);
            setTacking(new Trackingactivos());
            getTacking().setActivo(activo);
            String custodio = null;
            if (activo.getExterno() == null) {
                esExterno = false;
                externoEntidad = new Externos();
                if (activo.getCustodio() != null) {
                    custodio = "" + activo.getCustodio().getEntidad().toString();
                }
            } else {
                esExterno = true;
                externoEntidad = activo.getExterno();
                custodio = "" + externoEntidad.toString();
            }
            getTacking().setDescripcion(custodio);
            getTacking().setCuenta1(custodio);
            getTacking().setFecha(new Date());
            getTacking().setTipo(1);
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

    private boolean validar() {
//        if ((activo.getCodigo() == null) || (activo.getCodigo().isEmpty())) {
//            MensajesErrores.advertencia("C??digo es Obligatorio es obligatorio");
//            return true;
//        }
        if (esExterno) {
            if ((externoEntidad.getEdificio() == null)) {
                MensajesErrores.advertencia("Edificio es obligatorio");
                return true;
            }
            if ((externoEntidad.getEmpresa() == null) || (externoEntidad.getEmpresa().isEmpty())) {
                MensajesErrores.advertencia("RUC Empresa es obligatoria");
                return true;
            }
            if ((externoEntidad.getNombre() == null) || (externoEntidad.getNombre().isEmpty())) {
                MensajesErrores.advertencia("Nombre es obligatorio");
                return true;
            }
            activo.setExterno(externoEntidad);
            activo.setCustodio(null);
        } else {
            if (entidad == null) {
                MensajesErrores.advertencia("Custodio es necesario");
                return true;
            }
            if (entidad.getEmpleados() == null) {
                MensajesErrores.advertencia("Custodio, no es empleado");
                return true;
            }
            activo.setExterno(null);
            activo.setCustodio(entidad.getEmpleados());
        }
        return false;
    }

    public String grabar() {
        if (validar()) {
            return null;
        }
        try {
            //            *************************************incrementar para externo********
            String custodio = "";
            if (esExterno) {
                if (externoEntidad.getId() == null) {
                    ejbExternos.create(externoEntidad, getSeguridadbean().getLogueado().getUserid());
                } else {
                    ejbExternos.edit(externoEntidad, getSeguridadbean().getLogueado().getUserid());
                }
                activo.setExterno(externoEntidad);
                custodio = "" + externoEntidad.toString();
            } else {
                custodio = "" + activo.getCustodio().getEntidad().toString();
            }
            tacking.setCuenta2(custodio);
            activo.setActa(null);
            ejbActivos.edit(activo, getSeguridadbean().getLogueado().getUserid());
            ejbTracking.create(getTacking(), getSeguridadbean().getLogueado().getUserid());
            //N??mero de acta
            Codigos tipoActa = codigosBean.traerCodigo(Constantes.ACTAS, "03");
            if (tipoActa == null) {
                MensajesErrores.advertencia("No existe tipo de acta de c??digo 03");
                return null;
            }
            Codigos configuracion = codigosBean.traerCodigo(Constantes.CONFIGURACION_ACTAS, "06");
            if (configuracion == null) {
                MensajesErrores.advertencia("No existe configuraci??n de acta de c??digo 03");
                return null;
            }
            Actas actaNueva = new Actas();
            actaNueva.setAceptacion(configuracion.getNombre());
            actaNueva.setAntecedentes(configuracion.getDescripcion());
            actaNueva.setTipo(tipoActa);
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
            actaNueva.setEntregan(seguridadbean.getLogueado().toString() + "@" + "UNIDAD DE BIENES@");
            if (entidad != null) {
                actaNueva.setReciben(entidad.toString() + "@" + "ADMINISTRADOR DEL CONTRATO@");
            } else {
                actaNueva.setReciben(externoEntidad.getNombre() + "@" + "ADMINISTRADOR DEL CONTRATO@");
            }
            Integer numeroNuevo = num++;
            tipoActa.setParametros("" + numeroNuevo);
            ejbCodigos.edit(tipoActa, seguridadbean.getLogueado().getUserid());
            ejbActas.create(actaNueva, seguridadbean.getLogueado().getUserid());
            Actasactivos aa = new Actasactivos();
            aa.setActa(actaNueva);
            aa.setActivo(activo);
            ejbActasactivos.create(aa, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        formularioImprimir.editar();
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
        if (activos.getRowData() == null) {
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
//            setListaEntidades(ejbEntidades.encontarParametros(parametros));
            listaEntidades = ejbEntidades.encontarParametros(parametros);

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }

    }

    public void cambiaApellido(ValueChangeEvent event) {
        if (listaEntidades == null) {
            return;
        }
        entidad = null;
        externoEntidad = null;
        String newWord = (String) event.getNewValue();
        for (Entidades e : listaEntidades) {
            if (e.getApellidos().compareToIgnoreCase(newWord) == 0) {
                entidad = e;
            }
        }

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

    public String cambiaTipoExterno() {
        if (esExterno) {
            getFormularioExterno().insertar();
        } else {
            getFormularioExterno().cancelar();
        }
        return null;
    }

    /**
     * @return the formularioExterno
     */
    public Formulario getFormularioExterno() {
        return formularioExterno;
    }

    /**
     * @param formularioExterno the formularioExterno to set
     */
    public void setFormularioExterno(Formulario formularioExterno) {
        this.formularioExterno = formularioExterno;
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

    public String reporte(Activos activo) {
        this.activo = activo;
        String numeroActa = null;
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.id=:id");
            parametros.put("id", activo.getId());
            List<Activos> listaActivos = ejbActivos.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put(";where", "o.activo=:activo and o.acta.tipo.codigo='03'");
            parametros.put("activo", activo);
            List<Actasactivos> listaAA = ejbActasactivos.encontarParametros(parametros);
            if (!listaAA.isEmpty()) {
                Actasactivos aa = listaAA.get(0);
                numeroActa = aa.getActa().getNumerotexto();
            }
            pdf = new DocumentoPDF("EMPRESA PUBLICA METROPOLITANA DE LOGISTICA PARA LA SEGURIDAD", null, seguridadbean.getLogueado().getUserid());
            pdf.agregaParrafoCompleto("ACTIVOS FIJOS", AuxiliarReporte.ALIGN_CENTER, 9, false);
            pdf.agregaParrafoCompleto("ACTA DE ENTREGA-RECEPCI??N", AuxiliarReporte.ALIGN_CENTER, 9, false);
            pdf.agregaParrafoCompleto("ACTA NRO. " + (numeroActa != null ? numeroActa : ""), AuxiliarReporte.ALIGN_CENTER, 9, false);
            pdf.agregaParrafoCompleto("\n\n", AuxiliarReporte.ALIGN_CENTER, 9, false);

            Date fecha = activo.getFechaingreso();
            String anio = new SimpleDateFormat("yyyy").format(fecha);
            String mes = new SimpleDateFormat("MM").format(fecha);
            String nomMes = seguridadbean.traerNombreMes(Integer.parseInt(mes)).toLowerCase();
            String dia = new SimpleDateFormat("dd").format(fecha);
            String custodio = activo.getCustodio() != null ? activo.getCustodio().toString() : "";
            String cedula = activo.getCustodio() != null ? activo.getCustodio().getEntidad().getPin() : "";

            pdf.agregaParrafoCompleto(("En la ciudad de Quito, a los " + dia + " dias del mes de " + nomMes + " del " + anio + " en forma libre y voluntaria comparecen por la parte "
                    + "los se??ores en sus respectivas calidades: BODEGA CENTRAL-, quien entrega los bienes y el Sr.(a/ita). " + custodio + ", quien los "
                    + "recibe en calidad de custodio de los bienes, de la Empresa Publica Metropolitana de Logistica para la Seguridad con el objeto de "
                    + "realizar la presente acta de Entrega-Recepci??n General de los bienes institucionales que a contuniaci??n se detallan:"), AuxiliarReporte.ALIGN_JUSTIFIED, 9, false);
            pdf.agregaParrafoCompleto("\n\n", AuxiliarReporte.ALIGN_CENTER, 9, false);
            List<AuxiliarReporte> valores = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 7, AuxiliarReporte.ALIGN_LEFT, 7, true, "CODIGO" + "\n" + "ACTIVO"));
            titulos.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_LEFT, 7, true, "DESCRIPCION"));
            titulos.add(new AuxiliarReporte("String", 7, AuxiliarReporte.ALIGN_LEFT, 7, true, "MARCA"));
            titulos.add(new AuxiliarReporte("String", 7, AuxiliarReporte.ALIGN_LEFT, 7, true, "MODELO"));
            titulos.add(new AuxiliarReporte("String", 7, AuxiliarReporte.ALIGN_LEFT, 7, true, "ESTADO DEL BIEN"));
            titulos.add(new AuxiliarReporte("String", 5, AuxiliarReporte.ALIGN_LEFT, 7, true, "SERIE"));
            titulos.add(new AuxiliarReporte("String", 7, AuxiliarReporte.ALIGN_RIGHT, 7, true, "VALOR"));

            for (Activos a : listaActivos) {
                valores.add(new AuxiliarReporte("String", 7, AuxiliarReporte.ALIGN_LEFT, 7, false, a.getCodigo()));
                valores.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_LEFT, 7, false, a.getDescripcion()));
                valores.add(new AuxiliarReporte("String", 7, AuxiliarReporte.ALIGN_LEFT, 7, false, a.getActivomarca() != null ? a.getActivomarca().getNombre() : ""));
                valores.add(new AuxiliarReporte("String", 7, AuxiliarReporte.ALIGN_LEFT, 7, false, a.getModelo()));
                valores.add(new AuxiliarReporte("String", 7, AuxiliarReporte.ALIGN_LEFT, 7, false, a.getEstado().getNombre()));
                valores.add(new AuxiliarReporte("String", 5, AuxiliarReporte.ALIGN_LEFT, 7, false, a.getNumeroserie()));
                valores.add(new AuxiliarReporte("Double", 7, AuxiliarReporte.ALIGN_RIGHT, 7, false, a.getValoralta().doubleValue()));
            }
            pdf.agregarTablaReporte(titulos, valores, 7, 100, "");
            pdf.agregaParrafo("\n");
            double iva = 0.00;
            valores = new LinkedList<>();
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Subtotal: "));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, ""));
            valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_RIGHT, 7, false, activo.getValoralta().doubleValue()));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "IVA: "));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, ""));
            valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_RIGHT, 7, false, activo.getIva() == null ? iva : (activo.getIva().equals(0.00)) ? iva : (activo.getValoralta().doubleValue() * activo.getIva().doubleValue())));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Total: "));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, listaActivos.size() + ""));
            valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_RIGHT, 7, false, activo.getIva() == null ? (activo.getValoralta().doubleValue()) : (activo.getIva().equals(0.00)) ? (activo.getValoralta().doubleValue()) : (activo.getValoralta().doubleValue() + (activo.getValoralta().doubleValue() * activo.getIva().doubleValue()))));
            pdf.agregarTabla(null, valores, 3, 100, "");
            pdf.agregaParrafo("\n");
            pdf.agregaParrafo("OBSERVACI??N :");
            pdf.agregaParrafo("Esta entrega recepci??n se sujeta a las siguientes cl??usulas conforme establece el Reglamento"
                    + "General Sustitutivo pare el Manejo y Administraci??n de bienes del Sector P??blico:\n"
                    + "??? Todos los bienes descritos en la presente Acta ser??n de exclusiva responsabilidad, buen uso y cuidado de quien(es) reciben.\n"
                    + "??? En caso de cambio, retiro o incremento de bienes, estos deber??n ser notificados al la Unidad de Gesti??n de Bienes, para su actualizaci??n, registro y control.\n"
                    + "??? En caso de perdida, deber?? notificar a trav??s de su Jefe Inmediato o a la M??xima Autoridad.\n "
                    + "??? En caso de establecer responsabilidad en su contra, la reposici??n de los bienes, se har?? en dinero a precio del mercado o en especies de iguales "
                    + "caracteristicas del bien desaparecido, destruido o inutilizado.\n");
            pdf.agregaParrafo("\n");
//            Empleados e = null;
//            parametros = new HashMap();
//            parametros.put(";where", "o.cargoactual.descripcion='RESPONSABLE DE UNIDAD DE BIENES'");
//            List<Empleados> lista = ejbEmpleados.encontarParametros(parametros);
//            if (!lista.isEmpty()) {
//                e = lista.get(0);
//            }
            Codigos bien = codigosBean.traerCodigo(Constantes.RESPONSABLE_UNIDAD, "01");
            if (bien == null) {
                MensajesErrores.advertencia("No existe Responsable de Bienes");
                return null;
            }
            valores = new LinkedList<>();
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "ENTREGUE CONFORME"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "RECIBO CONFORME"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "REVISADO"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "\n\n\n\n\n"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "\n\n\n\n\n"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "\n\n\n\n\n"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "_______________________________"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "_______________________________"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "_______________________________"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "BODEGA-BIENES DEVUELTOS"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, custodio));
//            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "CONTROL DE BIENES\n" + (e != null ? e.toString() : "")));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "CONTROL DE BIENES\n" + (bien.getDescripcion() != null ? bien.getDescripcion() : "")));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "C.I.:"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "C.I.:" + cedula));
//            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "C.I.:" + (e != null ? e.getEntidad().getPin() : "")));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "C.I.:" + (bien.getParametros() != null ? bien.getParametros() : "")));
            pdf.agregarTabla(null, valores, 3, 100, "");
            reportepdf = pdf.traerRecurso();

        } catch (ConsultarException | IOException | DocumentException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger(CambioCustodioActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        nombrepdf = "Cambio Custodio-" + numeroActa + ".pdf";
        formularioImprimi.insertar();
        return null;
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
     * @return the formularioImprimi
     */
    public Formulario getFormularioImprimi() {
        return formularioImprimi;
    }

    /**
     * @param formularioImprimi the formularioImprimi to set
     */
    public void setFormularioImprimi(Formulario formularioImprimi) {
        this.formularioImprimi = formularioImprimi;
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
