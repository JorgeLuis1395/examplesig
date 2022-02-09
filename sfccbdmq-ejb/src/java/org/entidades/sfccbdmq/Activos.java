/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "activos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Activos.findAll", query = "SELECT a FROM Activos a"),
    @NamedQuery(name = "Activos.findById", query = "SELECT a FROM Activos a WHERE a.id = :id"),
    @NamedQuery(name = "Activos.findByDescripcion", query = "SELECT a FROM Activos a WHERE a.descripcion = :descripcion"),
    @NamedQuery(name = "Activos.findByMarca", query = "SELECT a FROM Activos a WHERE a.marca = :marca"),
    @NamedQuery(name = "Activos.findByModelo", query = "SELECT a FROM Activos a WHERE a.modelo = :modelo"),
    @NamedQuery(name = "Activos.findByAniofabricacion", query = "SELECT a FROM Activos a WHERE a.aniofabricacion = :aniofabricacion"),
    @NamedQuery(name = "Activos.findByColor", query = "SELECT a FROM Activos a WHERE a.color = :color"),
    @NamedQuery(name = "Activos.findByImagen", query = "SELECT a FROM Activos a WHERE a.imagen = :imagen"),
    @NamedQuery(name = "Activos.findByDepreciable", query = "SELECT a FROM Activos a WHERE a.depreciable = :depreciable"),
    @NamedQuery(name = "Activos.findByCodigo", query = "SELECT a FROM Activos a WHERE a.codigo = :codigo"),
    @NamedQuery(name = "Activos.findByInventario", query = "SELECT a FROM Activos a WHERE a.inventario = :inventario"),
    @NamedQuery(name = "Activos.findByAlterno", query = "SELECT a FROM Activos a WHERE a.alterno = :alterno"),
    @NamedQuery(name = "Activos.findByBarras", query = "SELECT a FROM Activos a WHERE a.barras = :barras"),
    @NamedQuery(name = "Activos.findByVidautil", query = "SELECT a FROM Activos a WHERE a.vidautil = :vidautil"),
    @NamedQuery(name = "Activos.findByValoradquisiscion", query = "SELECT a FROM Activos a WHERE a.valoradquisiscion = :valoradquisiscion"),
    @NamedQuery(name = "Activos.findByFechaalta", query = "SELECT a FROM Activos a WHERE a.fechaalta = :fechaalta"),
    @NamedQuery(name = "Activos.findByValoralta", query = "SELECT a FROM Activos a WHERE a.valoralta = :valoralta"),
    @NamedQuery(name = "Activos.findByValorreposicion", query = "SELECT a FROM Activos a WHERE a.valorreposicion = :valorreposicion"),
    @NamedQuery(name = "Activos.findByFechadepreciacion", query = "SELECT a FROM Activos a WHERE a.fechadepreciacion = :fechadepreciacion"),
    @NamedQuery(name = "Activos.findByFechabaja", query = "SELECT a FROM Activos a WHERE a.fechabaja = :fechabaja"),
    @NamedQuery(name = "Activos.findByCausa", query = "SELECT a FROM Activos a WHERE a.causa = :causa"),
    @NamedQuery(name = "Activos.findByNombre", query = "SELECT a FROM Activos a WHERE a.nombre = :nombre"),
    @NamedQuery(name = "Activos.findByValorresidual", query = "SELECT a FROM Activos a WHERE a.valorresidual = :valorresidual"),
    @NamedQuery(name = "Activos.findByNumeroserie", query = "SELECT a FROM Activos a WHERE a.numeroserie = :numeroserie"),
    @NamedQuery(name = "Activos.findByFechaingreso", query = "SELECT a FROM Activos a WHERE a.fechaingreso = :fechaingreso"),
    @NamedQuery(name = "Activos.findByObservaciones", query = "SELECT a FROM Activos a WHERE a.observaciones = :observaciones"),
    @NamedQuery(name = "Activos.findByFechasolicitud", query = "SELECT a FROM Activos a WHERE a.fechasolicitud = :fechasolicitud"),
    @NamedQuery(name = "Activos.findByFvengarantia", query = "SELECT a FROM Activos a WHERE a.fvengarantia = :fvengarantia"),
    @NamedQuery(name = "Activos.findByUnigarantia", query = "SELECT a FROM Activos a WHERE a.unigarantia = :unigarantia"),
    @NamedQuery(name = "Activos.findByGarantia", query = "SELECT a FROM Activos a WHERE a.garantia = :garantia")})
public class Activos implements Serializable {

    @Column(name = "auxiliar")
    private Integer auxiliar;
    @Size(max = 2147483647)
    @Column(name = "directorioimagen")
    private String directorioimagen;

    @JoinColumn(name = "contrato", referencedColumnName = "id")
    @ManyToOne
    private Contratos contrato;

    @Size(max = 2147483647)
    @Column(name = "tipoauxiliar")
    private String tipoauxiliar;
    @Size(max = 2147483647)
    @Column(name = "nominativo")
    private String nominativo;

    @Size(max = 2147483647)
    @Column(name = "usuario")
    private String usuario;
    @JoinColumn(name = "activomarca", referencedColumnName = "id")
    @ManyToOne
    private Marcas activomarca;

    @Size(max = 2147483647)
    @Column(name = "dimensiones")
    private String dimensiones;
    @Size(max = 2147483647)
    @Column(name = "caracteristicas")
    private String caracteristicas;
    @OneToMany(mappedBy = "activo")
    private List<Activoobligacion> activoobligacionList;
    @OneToMany(mappedBy = "activo")
    private List<Trackingactivos> trackingactivosList;
    @OneToMany(mappedBy = "activo")
    private List<Depreciaciones> depreciacionesList;
    @OneToMany(mappedBy = "activo")
    private List<Reclamos> reclamosList;
    @OneToMany(mappedBy = "activo")
    private List<Vidasutiles> vidasutilesList;
    @OneToMany(mappedBy = "activo")
    private List<Activopoliza> activopolizaList;
    @OneToMany(mappedBy = "activo")
    private List<Actasactivos> actasactivosList;
    @OneToMany(mappedBy = "activo")
    private List<Erogaciones> erogacionesList;
    @Column(name = "factura")
    private String factura;
    @Column(name = "acta")
    private Integer acta;
    @Column(name = "actaultima")
    private Integer actaultima;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "activo")
    private List<Detalletoma> detalletomaList;
    @Column(name = "operativo")
    private Boolean operativo;
    @JoinColumn(name = "subgrupo", referencedColumnName = "id")
    @ManyToOne
    private Subgruposactivos subgrupo;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "descripcion")
    private String descripcion;
    @Size(max = 2147483647)
    @Column(name = "marca")
    private String marca;
    @Size(max = 2147483647)
    @Column(name = "modelo")
    private String modelo;
    @Column(name = "aniofabricacion")
    private Integer aniofabricacion;
    @Size(max = 2147483647)
    @Column(name = "color")
    private String color;
    @Column(name = "imagen")
    private Integer imagen;
    @Column(name = "depreciable")
    private Boolean depreciable;
    @Size(max = 2147483647)
    @Column(name = "codigo")
    private String codigo;
    @Size(max = 2147483647)
    @Column(name = "inventario")
    private String inventario;
    @Size(max = 2147483647)
    @Column(name = "alterno")
    private String alterno;
    @Size(max = 2147483647)
    @Column(name = "barras")
    private String barras;
    @Column(name = "vidautil")
    private Integer vidautil;
    @Column(name = "control")
    private Boolean control;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valoradquisiscion")
    private BigDecimal valoradquisiscion;
    @Column(name = "fechaalta")
    @Temporal(TemporalType.DATE)
    private Date fechaalta;
    @Column(name = "fechaconta")
    @Temporal(TemporalType.DATE)
    private Date fechaconta;
    @Column(name = "valoralta")
    private BigDecimal valoralta;
    @Column(name = "valorreposicion")
    private BigDecimal valorreposicion;
    @Column(name = "fechadepreciacion")
    @Temporal(TemporalType.DATE)
    private Date fechadepreciacion;
    @Column(name = "fechabaja")
    @Temporal(TemporalType.DATE)
    private Date fechabaja;
    @Column(name = "fechapuestaenservicio")
    @Temporal(TemporalType.DATE)
    private Date fechapuestaenservicio;
    @Size(max = 2147483647)
    @Column(name = "causa")
    private String causa;
    @Size(max = 2147483647)
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "valorresidual")
    private Float valorresidual;
    @Size(max = 2147483647)
    @Column(name = "numeroserie")
    private String numeroserie;
    @Column(name = "fechaingreso")
    @Temporal(TemporalType.DATE)
    private Date fechaingreso;
    @Size(max = 2147483647)
    @Column(name = "observaciones")
    private String observaciones;
    @Column(name = "ccosto")
    private String ccosto;
    @Column(name = "fechasolicitud")
    @Temporal(TemporalType.DATE)
    private Date fechasolicitud;
    @Column(name = "fvengarantia")
    @Temporal(TemporalType.DATE)
    private Date fvengarantia;
    @Column(name = "unigarantia")
    private Float unigarantia;
    @Column(name = "garantia")
    private Boolean garantia;
    @JoinColumn(name = "baja", referencedColumnName = "id")
    @ManyToOne
    private Tipomovactivos baja;
    @JoinColumn(name = "alta", referencedColumnName = "id")
    @ManyToOne
    private Tipomovactivos alta;
    @OneToMany(mappedBy = "padre")
    private List<Activos> activosList;
    @JoinColumn(name = "padre", referencedColumnName = "id")
    @ManyToOne
    private Activos padre;
    @JoinColumn(name = "clasificacion", referencedColumnName = "id")
    @ManyToOne
    private Codigos clasificacion;
    @JoinColumn(name = "estado", referencedColumnName = "id")
    @ManyToOne
    private Codigos estado;
    @JoinColumn(name = "autoriza", referencedColumnName = "id")
    @ManyToOne
    private Empleados autoriza;
    @JoinColumn(name = "custodio", referencedColumnName = "id")
    @ManyToOne
    private Empleados custodio;
    @JoinColumn(name = "solicitante", referencedColumnName = "id")
    @ManyToOne
    private Empleados solicitante;
    @JoinColumn(name = "externo", referencedColumnName = "id")
    @ManyToOne
    private Externos externo;
    @JoinColumn(name = "grupo", referencedColumnName = "id")
    @ManyToOne
    private Grupoactivos grupo;
    @JoinColumn(name = "localizacion", referencedColumnName = "id")
    @ManyToOne
    private Oficinas localizacion;
    @JoinColumn(name = "poliza", referencedColumnName = "id")
    @ManyToOne
    private Polizas poliza;
    @JoinColumn(name = "proveedor", referencedColumnName = "id")
    @ManyToOne
    private Proveedores proveedor;
    @JoinColumn(name = "proyecto", referencedColumnName = "id")
    @ManyToOne
    private Proyectos proyecto;
    @Column(name = "iva")
    private BigDecimal iva;
    @Transient
    private double depreciacionAcumulada;
    @Transient
    private boolean seleccionado;
    @Transient
    private Boolean escojer;
    @Transient
    private String activoMarc;
    @Transient
    private String f1Srt;
    @Transient
    private String f2Srt;
    @Transient
    private String f3Srt;
    @Transient
    private String grupoSrt;
    @Transient
    private String subgrupoSrt;
    @Transient
    private BigDecimal valorAltaSinIva;
    @Transient
    private String nombreImagen;
    public Activos() {
    }

    public Activos(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Integer getAniofabricacion() {
        return aniofabricacion;
    }

    public void setAniofabricacion(Integer aniofabricacion) {
        this.aniofabricacion = aniofabricacion;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getImagen() {
        return imagen;
    }

    public void setImagen(Integer imagen) {
        this.imagen = imagen;
    }

    public Boolean getDepreciable() {
        return depreciable;
    }

    public void setDepreciable(Boolean depreciable) {
        this.depreciable = depreciable;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getInventario() {
        return inventario;
    }

    public void setInventario(String inventario) {
        this.inventario = inventario;
    }

    public String getAlterno() {
        return alterno;
    }

    public void setAlterno(String alterno) {
        this.alterno = alterno;
    }

    public String getBarras() {
        return barras;
    }

    public void setBarras(String barras) {
        this.barras = barras;
    }

    public Integer getVidautil() {
        return vidautil;
    }

    public void setVidautil(Integer vidautil) {
        this.vidautil = vidautil;
    }

    public BigDecimal getValoradquisiscion() {
        return valoradquisiscion;
    }

    public void setValoradquisiscion(BigDecimal valoradquisiscion) {
        this.valoradquisiscion = valoradquisiscion;
    }

    public Date getFechaalta() {
        return fechaalta;
    }

    public void setFechaalta(Date fechaalta) {
        this.fechaalta = fechaalta;
    }

    public BigDecimal getValoralta() {
        return valoralta;
    }

    public void setValoralta(BigDecimal valoralta) {
        this.valoralta = valoralta;
    }

    public BigDecimal getValorreposicion() {
        return valorreposicion;
    }

    public void setValorreposicion(BigDecimal valorreposicion) {
        this.valorreposicion = valorreposicion;
    }

    public Date getFechadepreciacion() {
        return fechadepreciacion;
    }

    public void setFechadepreciacion(Date fechadepreciacion) {
        this.fechadepreciacion = fechadepreciacion;
    }

    public Date getFechabaja() {
        return fechabaja;
    }

    public void setFechabaja(Date fechabaja) {
        this.fechabaja = fechabaja;
    }

    public String getCausa() {
        return causa;
    }

    public void setCausa(String causa) {
        this.causa = causa;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Float getValorresidual() {
        return valorresidual;
    }

    public void setValorresidual(Float valorresidual) {
        this.valorresidual = valorresidual;
    }

    public String getNumeroserie() {
        return numeroserie;
    }

    public void setNumeroserie(String numeroserie) {
        this.numeroserie = numeroserie;
    }

    public Date getFechaingreso() {
        return fechaingreso;
    }

    public void setFechaingreso(Date fechaingreso) {
        this.fechaingreso = fechaingreso;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Date getFechasolicitud() {
        return fechasolicitud;
    }

    public void setFechasolicitud(Date fechasolicitud) {
        this.fechasolicitud = fechasolicitud;
    }

    public Date getFvengarantia() {
        return fvengarantia;
    }

    public void setFvengarantia(Date fvengarantia) {
        this.fvengarantia = fvengarantia;
    }

    public Float getUnigarantia() {
        return unigarantia;
    }

    public void setUnigarantia(Float unigarantia) {
        this.unigarantia = unigarantia;
    }

    public Boolean getGarantia() {
        return garantia;
    }

    public void setGarantia(Boolean garantia) {
        this.garantia = garantia;
    }

    public Tipomovactivos getBaja() {
        return baja;
    }

    public void setBaja(Tipomovactivos baja) {
        this.baja = baja;
    }

    public Tipomovactivos getAlta() {
        return alta;
    }

    public void setAlta(Tipomovactivos alta) {
        this.alta = alta;
    }

    @XmlTransient
    public List<Activos> getActivosList() {
        return activosList;
    }

    public void setActivosList(List<Activos> activosList) {
        this.activosList = activosList;
    }

    public Activos getPadre() {
        return padre;
    }

    public void setPadre(Activos padre) {
        this.padre = padre;
    }

    public Codigos getClasificacion() {
        return clasificacion;
    }

    public void setClasificacion(Codigos clasificacion) {
        this.clasificacion = clasificacion;
    }

    public Codigos getEstado() {
        return estado;
    }

    public void setEstado(Codigos estado) {
        this.estado = estado;
    }

    public Empleados getAutoriza() {
        return autoriza;
    }

    public void setAutoriza(Empleados autoriza) {
        this.autoriza = autoriza;
    }

    public Empleados getCustodio() {
        return custodio;
    }

    public void setCustodio(Empleados custodio) {
        this.custodio = custodio;
    }

    public Empleados getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(Empleados solicitante) {
        this.solicitante = solicitante;
    }

    public Externos getExterno() {
        return externo;
    }

    public void setExterno(Externos externo) {
        this.externo = externo;
    }

    public Grupoactivos getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupoactivos grupo) {
        this.grupo = grupo;
    }

    public Oficinas getLocalizacion() {
        return localizacion;
    }

    public void setLocalizacion(Oficinas localizacion) {
        this.localizacion = localizacion;
    }

    public Polizas getPoliza() {
        return poliza;
    }

    public void setPoliza(Polizas poliza) {
        this.poliza = poliza;
    }

    public Proveedores getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedores proveedor) {
        this.proveedor = proveedor;
    }

    public Proyectos getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyectos proyecto) {
        this.proyecto = proyecto;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Activos)) {
            return false;
        }
        Activos other = (Activos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return id + "";
    }

    /**
     * @return the control
     */
    public Boolean getControl() {
        return control;
    }

    /**
     * @param control the control to set
     */
    public void setControl(Boolean control) {
        this.control = control;
    }

    public Boolean getOperativo() {
        return operativo;
    }

    public void setOperativo(Boolean operativo) {
        this.operativo = operativo;
    }

    public Subgruposactivos getSubgrupo() {
        return subgrupo;
    }

    public void setSubgrupo(Subgruposactivos subgrupo) {
        this.subgrupo = subgrupo;
    }

    @XmlTransient
    public List<Detalletoma> getDetalletomaList() {
        return detalletomaList;
    }

    public void setDetalletomaList(List<Detalletoma> detalletomaList) {
        this.detalletomaList = detalletomaList;
    }

    /**
     * @return the ccosto
     */
    public String getCcosto() {
        return ccosto;
    }

    /**
     * @param ccosto the ccosto to set
     */
    public void setCcosto(String ccosto) {
        this.ccosto = ccosto;
    }

    /**
     * @return the depreciacionAcumulada
     */
    public double getDepreciacionAcumulada() {
        return depreciacionAcumulada;
    }

    /**
     * @param depreciacionAcumulada the depreciacionAcumulada to set
     */
    public void setDepreciacionAcumulada(double depreciacionAcumulada) {
        this.depreciacionAcumulada = depreciacionAcumulada;
    }

    public String getFactura() {
        return factura;
    }

    public void setFactura(String factura) {
        this.factura = factura;
    }

    /**
     * @return the seleccionado
     */
    public boolean isSeleccionado() {
        return seleccionado;
    }

    /**
     * @param seleccionado the seleccionado to set
     */
    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

    /**
     * @return the acta
     */
    public Integer getActa() {
        return acta;
    }

    /**
     * @param acta the acta to set
     */
    public void setActa(Integer acta) {
        this.acta = acta;
    }

    /**
     * @return the actaultima
     */
    public Integer getActaultima() {
        return actaultima;
    }

    /**
     * @param actaultima the actaultima to set
     */
    public void setActaultima(Integer actaultima) {
        this.actaultima = actaultima;
    }

    /**
     * @return the fechapuestaenservicio
     */
    public Date getFechapuestaenservicio() {
        return fechapuestaenservicio;
    }

    /**
     * @param fechapuestaenservicio the fechapuestaenservicio to set
     */
    public void setFechapuestaenservicio(Date fechapuestaenservicio) {
        this.fechapuestaenservicio = fechapuestaenservicio;
    }

    /**
     * @return the iva
     */
    public BigDecimal getIva() {
        return iva;
    }

    /**
     * @param iva the iva to set
     */
    public void setIva(BigDecimal iva) {
        this.iva = iva;
    }

    @XmlTransient
    public List<Erogaciones> getErogacionesList() {
        return erogacionesList;
    }

    public void setErogacionesList(List<Erogaciones> erogacionesList) {
        this.erogacionesList = erogacionesList;
    }

    @XmlTransient
    public List<Actasactivos> getActasactivosList() {
        return actasactivosList;
    }

    public void setActasactivosList(List<Actasactivos> actasactivosList) {
        this.actasactivosList = actasactivosList;
    }

    public String getDimensiones() {
        return dimensiones;
    }

    public void setDimensiones(String dimensiones) {
        this.dimensiones = dimensiones;
    }

    public String getCaracteristicas() {
        return caracteristicas;
    }

    public void setCaracteristicas(String caracteristicas) {
        this.caracteristicas = caracteristicas;
    }

    @XmlTransient
    public List<Activoobligacion> getActivoobligacionList() {
        return activoobligacionList;
    }

    public void setActivoobligacionList(List<Activoobligacion> activoobligacionList) {
        this.activoobligacionList = activoobligacionList;
    }

    @XmlTransient
    public List<Trackingactivos> getTrackingactivosList() {
        return trackingactivosList;
    }

    public void setTrackingactivosList(List<Trackingactivos> trackingactivosList) {
        this.trackingactivosList = trackingactivosList;
    }

    @XmlTransient
    public List<Depreciaciones> getDepreciacionesList() {
        return depreciacionesList;
    }

    public void setDepreciacionesList(List<Depreciaciones> depreciacionesList) {
        this.depreciacionesList = depreciacionesList;
    }

    @XmlTransient
    public List<Reclamos> getReclamosList() {
        return reclamosList;
    }

    public void setReclamosList(List<Reclamos> reclamosList) {
        this.reclamosList = reclamosList;
    }

    @XmlTransient
    public List<Vidasutiles> getVidasutilesList() {
        return vidasutilesList;
    }

    public void setVidasutilesList(List<Vidasutiles> vidasutilesList) {
        this.vidasutilesList = vidasutilesList;
    }

    @XmlTransient
    public List<Activopoliza> getActivopolizaList() {
        return activopolizaList;
    }

    public void setActivopolizaList(List<Activopoliza> activopolizaList) {
        this.activopolizaList = activopolizaList;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Marcas getActivomarca() {
        return activomarca;
    }

    public void setActivomarca(Marcas activomarca) {
        this.activomarca = activomarca;
    }

    public String getTipoauxiliar() {
        return tipoauxiliar;
    }

    public void setTipoauxiliar(String tipoauxiliar) {
        this.tipoauxiliar = tipoauxiliar;
    }

    public String getNominativo() {
        return nominativo;
    }

    public void setNominativo(String nominativo) {
        this.nominativo = nominativo;
    }

    /**
     * @return the activoMarc
     */
    public String getActivoMarc() {
        return activoMarc;
    }

    /**
     * @param activoMarc the activoMarc to set
     */
    public void setActivoMarc(String activoMarc) {
        this.activoMarc = activoMarc;
    }

    public Contratos getContrato() {
        return contrato;
    }

    public void setContrato(Contratos contrato) {
        this.contrato = contrato;
    }

    /**
     * @return the f1Srt
     */
    public String getF1Srt() {
        return f1Srt;
    }

    /**
     * @param f1Srt the f1Srt to set
     */
    public void setF1Srt(String f1Srt) {
        this.f1Srt = f1Srt;
    }

    /**
     * @return the f2Srt
     */
    public String getF2Srt() {
        return f2Srt;
    }

    /**
     * @param f2Srt the f2Srt to set
     */
    public void setF2Srt(String f2Srt) {
        this.f2Srt = f2Srt;
    }

    /**
     * @return the f3Srt
     */
    public String getF3Srt() {
        return f3Srt;
    }

    /**
     * @param f3Srt the f3Srt to set
     */
    public void setF3Srt(String f3Srt) {
        this.f3Srt = f3Srt;
    }

    /**
     * @return the grupoSrt
     */
    public String getGrupoSrt() {
        return grupoSrt;
    }

    /**
     * @param grupoSrt the grupoSrt to set
     */
    public void setGrupoSrt(String grupoSrt) {
        this.grupoSrt = grupoSrt;
    }

    /**
     * @return the subgrupoSrt
     */
    public String getSubgrupoSrt() {
        return subgrupoSrt;
    }

    /**
     * @param subgrupoSrt the subgrupoSrt to set
     */
    public void setSubgrupoSrt(String subgrupoSrt) {
        this.subgrupoSrt = subgrupoSrt;
    }    

    /**
     * @return the escojer
     */
    public Boolean getEscojer() {
        return escojer;
    }

    /**
     * @param escojer the escojer to set
     */
    public void setEscojer(Boolean escojer) {
        this.escojer = escojer;
    }

    /**
     * @return the valorAltaSinIva
     */
    public BigDecimal getValorAltaSinIva() {
        return valorAltaSinIva;
    }

    /**
     * @param valorAltaSinIva the valorAltaSinIva to set
     */
    public void setValorAltaSinIva(BigDecimal valorAltaSinIva) {
        this.valorAltaSinIva = valorAltaSinIva;
    }

    /**
     * @return the fechaconta
     */
    public Date getFechaconta() {
        return fechaconta;
    }

    /**
     * @param fechaconta the fechaconta to set
     */
    public void setFechaconta(Date fechaconta) {
        this.fechaconta = fechaconta;
    }

    public Integer getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(Integer auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getDirectorioimagen() {
        return directorioimagen;
    }

    public void setDirectorioimagen(String directorioimagen) {
        this.directorioimagen = directorioimagen;
    }

    /**
     * @return the nombreImagen
     */
    public String getNombreImagen() {
        return nombreImagen;
    }

    /**
     * @param nombreImagen the nombreImagen to set
     */
    public void setNombreImagen(String nombreImagen) {
        this.nombreImagen = nombreImagen;
    }
}