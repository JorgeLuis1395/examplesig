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
import org.auxiliares.sfccbdmq.MensajesErrores;
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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.beans.sfccbdmq.ActivoobligacionFacade;
import org.beans.sfccbdmq.ActivosFacade;
import org.beans.sfccbdmq.DepreciacionesFacade;
import org.beans.sfccbdmq.TrackingactivosFacade;
import org.beans.sfccbdmq.VidasutilesFacade;
import org.entidades.sfccbdmq.Activoobligacion;
import org.entidades.sfccbdmq.Activos;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Depreciaciones;
import org.entidades.sfccbdmq.Edificios;
import org.entidades.sfccbdmq.Externos;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Grupoactivos;
import org.entidades.sfccbdmq.Oficinas;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Tipoactivo;
import org.entidades.sfccbdmq.Trackingactivos;
import org.entidades.sfccbdmq.Vidasutiles;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "ingresoUnidadesActivosSfccbdmq")
@ViewScoped
public class IngresoUnidadesActivosBean {

    /**
     * Creates a new instance of ActivoBean
     */
    public IngresoUnidadesActivosBean() {
        activos = new LazyDataModel<Activos>() {

            @Override
            public List<Activos> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }

    @EJB
    private ActivosFacade ejbActivos;
    @EJB
    private ActivoobligacionFacade ejbObligacion;
    @EJB
    private DepreciacionesFacade ejbDepreciaciones;
    @EJB
    private TrackingactivosFacade ejbTracking;
    @EJB
    private VidasutilesFacade ejbVida;
//    private int tamano;
    private String codigoAnterior;
    private Formatos nivel;
    private Activos activo;
    private Formulario formulario = new Formulario();
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
    private String marca;
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
    private Vidasutiles vida;
    private int mes;
    private int anio;
    private Externos externoEntidad = new Externos();
    private boolean esExterno = false;
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
    private Perfiles perfil;

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
        String nombreForma = "IngresoUnidadesActivosVista";
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
                String where = "  o.baja is null  and o.fechaalta is not null  and o.grupo.metododepreciacion.codigo='DEPHOR'";
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
                if (!((marca == null) || (marca.isEmpty()))) {
                    where += " and upper(o.marca) like :marca";
                    parametros.put("marca", marca.toUpperCase() + "%");
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
                if (grupo != null) {
                    where += " and o.grupo=:grupo";
                    parametros.put("grupo", grupo);
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
                if (proveedoresBean.getEmpresa() != null) {
                    where += " and o.proveedor=:proveedor";
                    parametros.put("proveedor", proveedoresBean.getEmpresa().getProveedores());
                }
                if (entidadesBean.getEntidad() != null) {
                    where += " and o.custodio=:custodio";
                    parametros.put("custodio", entidadesBean.getEntidad().getEmpleados());
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
        if (mes == 0) {
            MensajesErrores.advertencia("Favor coloque el mes a ingresar");
            return null;
        }
        if (anio == 0) {
            MensajesErrores.advertencia("Favor coloque el anio a ingresar");
            return null;
        }
        Calendar c=Calendar.getInstance();
        c.set(anio, mes-1, 1);
        if (c.before(configuracionBean.getConfiguracion().getPvigente())) {
            MensajesErrores.advertencia("Año o mes menor a periodo vigente");
            return null;
        }
        activo = (Activos) activos.getRowData();
        Map parametros = new HashMap();
        parametros.put(";where", "o.padre=:padre");
        parametros.put("padre", activo);
        try {
            hijos = ejbActivos.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put(";where", "o.activo=:padre");
            parametros.put("padre", activo);
            tackings = ejbTracking.encontarParametros(parametros);
            obligaciones = ejbObligacion.encontarParametros(parametros);
            depreciaciones = ejbDepreciaciones.encontarParametros(parametros);
            tacking = new Trackingactivos();
            tacking.setActivo(activo);
            tacking.setDescripcion("Modificación de Unidades de producción : " + String.valueOf(anio)+"/"+String.valueOf(mes));
            tacking.setFecha(new Date());
            tacking.setTipo(5);
            tacking.setUsuario(seguridadbean.getLogueado().getUserid());
            // buscar vida util
            parametros = new HashMap();
            parametros.put(";where", "o.activo=:activo and o.anio=:anio and o.mes=:mes");
            parametros.put("activo", activo);
            parametros.put("anio", anio);
            parametros.put("mes", mes);
            List<Vidasutiles> lvu = ejbVida.encontarParametros(parametros);
            vida = null;
            for (Vidasutiles vu : lvu) {
                vida = vu;
                tacking.setDescripcion("Ingreso de Unidades de producción : " + String.valueOf(anio)+"/"+String.valueOf(mes));
            }
            if (vida == null) {
                vida = new Vidasutiles();
                vida.setActivo(activo);
                vida.setAnio(anio);
                vida.setMes(mes);
                vida.setVidautil(activo.getVidautil());
                float porcentaje = (1 - (activo.getValorresidual() == null ? 0 : activo.getValorresidual()) / 100);
                float valorResidual = activo.getValoralta().floatValue() * porcentaje;
                vida.setValorresidual(valorResidual);
            }

        } catch (ConsultarException ex) {
            MensajesErrores.advertencia(ex.getMessage());
            Logger.getLogger(BajasActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (activo.getLocalizacion() != null) {
            oficinasBean.setEdificio(activo.getLocalizacion().getEdificio());
            oficinasBean.getComboOficinaEspacio();
        }
        formulario.editar();
        return null;
    }

    private boolean validar() {
        if ((vida.getUnidades()== null) || (vida.getUnidades()<=0)) {
            MensajesErrores.advertencia("Ingrese unidades del mes");
            return true;
        }

        return false;
    }

    public String grabar() {
        if (validar()) {
            return null;
        }

        try {
            ejbActivos.edit(activo, getSeguridadbean().getLogueado().getUserid());
            ejbTracking.create(tacking, getSeguridadbean().getLogueado().getUserid());
            if (vida.getId()==null){
                ejbVida.create(vida, getSeguridadbean().getLogueado().getUserid());
            } else {
                ejbVida.edit(vida, getSeguridadbean().getLogueado().getUserid());
            }

        } catch (GrabarException | InsertarException ex) {
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

    /**
     * @return the vida
     */
    public Vidasutiles getVida() {
        return vida;
    }

    /**
     * @param vida the vida to set
     */
    public void setVida(Vidasutiles vida) {
        this.vida = vida;
    }

    /**
     * @return the mes
     */
    public int getMes() {
        return mes;
    }

    /**
     * @param mes the mes to set
     */
    public void setMes(int mes) {
        this.mes = mes;
    }

    /**
     * @return the anio
     */
    public int getAnio() {
        return anio;
    }

    /**
     * @param anio the anio to set
     */
    public void setAnio(int anio) {
        this.anio = anio;
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
}
