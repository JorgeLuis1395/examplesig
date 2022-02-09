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
 * @author luis
 */
@Entity
@Table(name = "proyectospoa")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Proyectospoa.findAll", query = "SELECT p FROM Proyectospoa p")
    , @NamedQuery(name = "Proyectospoa.findById", query = "SELECT p FROM Proyectospoa p WHERE p.id = :id")
    , @NamedQuery(name = "Proyectospoa.findByCodigo", query = "SELECT p FROM Proyectospoa p WHERE p.codigo = :codigo")
    , @NamedQuery(name = "Proyectospoa.findByAnio", query = "SELECT p FROM Proyectospoa p WHERE p.anio = :anio")
    , @NamedQuery(name = "Proyectospoa.findByNombre", query = "SELECT p FROM Proyectospoa p WHERE p.nombre = :nombre")
    , @NamedQuery(name = "Proyectospoa.findByNivel", query = "SELECT p FROM Proyectospoa p WHERE p.nivel = :nivel")
    , @NamedQuery(name = "Proyectospoa.findByActivo", query = "SELECT p FROM Proyectospoa p WHERE p.activo = :activo")
    , @NamedQuery(name = "Proyectospoa.findByDefinitivo", query = "SELECT p FROM Proyectospoa p WHERE p.definitivo = :definitivo")
    , @NamedQuery(name = "Proyectospoa.findByAlineado", query = "SELECT p FROM Proyectospoa p WHERE p.alineado = :alineado")
    , @NamedQuery(name = "Proyectospoa.findByIngreso", query = "SELECT p FROM Proyectospoa p WHERE p.ingreso = :ingreso")
    , @NamedQuery(name = "Proyectospoa.findByImputable", query = "SELECT p FROM Proyectospoa p WHERE p.imputable = :imputable")
    , @NamedQuery(name = "Proyectospoa.findByObservaciones", query = "SELECT p FROM Proyectospoa p WHERE p.observaciones = :observaciones")
    , @NamedQuery(name = "Proyectospoa.findByInicio", query = "SELECT p FROM Proyectospoa p WHERE p.inicio = :inicio")
    , @NamedQuery(name = "Proyectospoa.findByTermino", query = "SELECT p FROM Proyectospoa p WHERE p.termino = :termino")
    , @NamedQuery(name = "Proyectospoa.findByDireccion", query = "SELECT p FROM Proyectospoa p WHERE p.direccion = :direccion")
    , @NamedQuery(name = "Proyectospoa.findByCpc", query = "SELECT p FROM Proyectospoa p WHERE p.cpc = :cpc")
    , @NamedQuery(name = "Proyectospoa.findByTipocompra", query = "SELECT p FROM Proyectospoa p WHERE p.tipocompra = :tipocompra")
    , @NamedQuery(name = "Proyectospoa.findByDetalle", query = "SELECT p FROM Proyectospoa p WHERE p.detalle = :detalle")
    , @NamedQuery(name = "Proyectospoa.findByCantidad", query = "SELECT p FROM Proyectospoa p WHERE p.cantidad = :cantidad")
    , @NamedQuery(name = "Proyectospoa.findByUnidad", query = "SELECT p FROM Proyectospoa p WHERE p.unidad = :unidad")
    , @NamedQuery(name = "Proyectospoa.findByValoriva", query = "SELECT p FROM Proyectospoa p WHERE p.valoriva = :valoriva")
    , @NamedQuery(name = "Proyectospoa.findByImpuesto", query = "SELECT p FROM Proyectospoa p WHERE p.impuesto = :impuesto")
    , @NamedQuery(name = "Proyectospoa.findByCuatrimestre1", query = "SELECT p FROM Proyectospoa p WHERE p.cuatrimestre1 = :cuatrimestre1")
    , @NamedQuery(name = "Proyectospoa.findByCuatrimestre2", query = "SELECT p FROM Proyectospoa p WHERE p.cuatrimestre2 = :cuatrimestre2")
    , @NamedQuery(name = "Proyectospoa.findByCuatrimestre3", query = "SELECT p FROM Proyectospoa p WHERE p.cuatrimestre3 = :cuatrimestre3")
    , @NamedQuery(name = "Proyectospoa.findByTipoproducto", query = "SELECT p FROM Proyectospoa p WHERE p.tipoproducto = :tipoproducto")
    , @NamedQuery(name = "Proyectospoa.findByCatalogoelectronico", query = "SELECT p FROM Proyectospoa p WHERE p.catalogoelectronico = :catalogoelectronico")
    , @NamedQuery(name = "Proyectospoa.findByProcedimientocontratacion", query = "SELECT p FROM Proyectospoa p WHERE p.procedimientocontratacion = :procedimientocontratacion")
    , @NamedQuery(name = "Proyectospoa.findByFondobid", query = "SELECT p FROM Proyectospoa p WHERE p.fondobid = :fondobid")
    , @NamedQuery(name = "Proyectospoa.findByNumerooperacion", query = "SELECT p FROM Proyectospoa p WHERE p.numerooperacion = :numerooperacion")
    , @NamedQuery(name = "Proyectospoa.findByCodigooperacion", query = "SELECT p FROM Proyectospoa p WHERE p.codigooperacion = :codigooperacion")
    , @NamedQuery(name = "Proyectospoa.findByRegimen", query = "SELECT p FROM Proyectospoa p WHERE p.regimen = :regimen")
    , @NamedQuery(name = "Proyectospoa.findByPresupuesto", query = "SELECT p FROM Proyectospoa p WHERE p.presupuesto = :presupuesto")
    , @NamedQuery(name = "Proyectospoa.findByFechautilizacion", query = "SELECT p FROM Proyectospoa p WHERE p.fechautilizacion = :fechautilizacion")})
public class Proyectospoa implements Serializable {

    @OneToMany(mappedBy = "proyecto")
    private List<Reformaspoa> reformaspoaList;

    @Column(name = "reformapac")
    private Integer reformapac;

    @Size(max = 2147483647)
    @Column(name = "direccionant")
    private String direccionant;

    @OneToMany(mappedBy = "proyecto")
    private List<Trackingproyectospoa> trackingproyectospoaList;

    @Size(max = 2147483647)
    @Column(name = "modificacion")
    private String modificacion;
    @Size(max = 2147483647)
    @Column(name = "responsable")
    private String responsable;
    @Size(max = 2147483647)
    @Column(name = "documento")
    private String documento;

    @Column(name = "fechacreacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechacreacion;
    @Column(name = "fechareforma")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechareforma;

    @Column(name = "pac")
    private Boolean pac;
    @Column(name = "fechapac")
    @Temporal(TemporalType.DATE)
    private Date fechapac;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "codigo")
    private String codigo;
    @Column(name = "anio")
    private Integer anio;
    @Size(max = 2147483647)
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "nivel")
    private Integer nivel;
    @Column(name = "activo")
    private Boolean activo;
    @Column(name = "definitivo")
    private Boolean definitivo;
    @Column(name = "alineado")
    private Boolean alineado;
    @Column(name = "ingreso")
    private Boolean ingreso;
    @Column(name = "imputable")
    private Boolean imputable;
    @Size(max = 2147483647)
    @Column(name = "observaciones")
    private String observaciones;
    @Column(name = "inicio")
    @Temporal(TemporalType.DATE)
    private Date inicio;
    @Column(name = "termino")
    @Temporal(TemporalType.DATE)
    private Date termino;
    @Size(max = 2147483647)
    @Column(name = "direccion")
    private String direccion;
    @Size(max = 2147483647)
    @Column(name = "cpc")
    private String cpc;
    @Size(max = 2147483647)
    @Column(name = "tipocompra")
    private String tipocompra;
    @Size(max = 2147483647)
    @Column(name = "detalle")
    private String detalle;
    @Column(name = "cantidad")
    private Integer cantidad;
    @Size(max = 2147483647)
    @Column(name = "unidad")
    private String unidad;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valoriva")
    private BigDecimal valoriva;
    @Column(name = "impuesto")
    private BigDecimal impuesto;
    @Column(name = "cuatrimestre1")
    private Boolean cuatrimestre1;
    @Column(name = "cuatrimestre2")
    private Boolean cuatrimestre2;
    @Column(name = "cuatrimestre3")
    private Boolean cuatrimestre3;
    @Size(max = 2147483647)
    @Column(name = "tipoproducto")
    private String tipoproducto;
    @Column(name = "catalogoelectronico")
    private Boolean catalogoelectronico;
    @Size(max = 2147483647)
    @Column(name = "procedimientocontratacion")
    private String procedimientocontratacion;
    @Column(name = "fondobid")
    private Boolean fondobid;
    @Size(max = 2147483647)
    @Column(name = "numerooperacion")
    private String numerooperacion;
    @Size(max = 2147483647)
    @Column(name = "codigooperacion")
    private String codigooperacion;
    @Size(max = 2147483647)
    @Column(name = "regimen")
    private String regimen;
    @Size(max = 2147483647)
    @Column(name = "presupuesto")
    private String presupuesto;
    @Column(name = "fechautilizacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechautilizacion;
    @OneToMany(mappedBy = "padre")
    private List<Proyectospoa> proyectospoaList;
    @JoinColumn(name = "padre", referencedColumnName = "id")
    @ManyToOne
    private Proyectospoa padre;
    @OneToMany(mappedBy = "proyecto")
    private List<Documentospoa> documentospoaList;
    @OneToMany(mappedBy = "proyecto")
    private List<Trackingspoa> trackingspoaList;
    @OneToMany(mappedBy = "proyecto")
    private List<Asignacionespoa> asignacionespoaList;
    @OneToMany(mappedBy = "proyecto")
    private List<Ejecucionpoa> ejecucionpoaList;
    @OneToMany(mappedBy = "proyecto")
    private List<Flujodecajapoa> flujodecajapoaList;
    @Transient
    private String cuat1Str;
    @Transient
    private String cuat2Str;
    @Transient
    private String cuat3Str;
    @Transient
    private String partidaStr;
  
    public Proyectospoa() {
    }

    public Proyectospoa(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Boolean getDefinitivo() {
        return definitivo;
    }

    public void setDefinitivo(Boolean definitivo) {
        this.definitivo = definitivo;
    }

    public Boolean getAlineado() {
        return alineado;
    }

    public void setAlineado(Boolean alineado) {
        this.alineado = alineado;
    }

    public Boolean getIngreso() {
        return ingreso;
    }

    public void setIngreso(Boolean ingreso) {
        this.ingreso = ingreso;
    }

    public Boolean getImputable() {
        return imputable;
    }

    public void setImputable(Boolean imputable) {
        this.imputable = imputable;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getTermino() {
        return termino;
    }

    public void setTermino(Date termino) {
        this.termino = termino;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCpc() {
        return cpc;
    }

    public void setCpc(String cpc) {
        this.cpc = cpc;
    }

    public String getTipocompra() {
        return tipocompra;
    }

    public void setTipocompra(String tipocompra) {
        this.tipocompra = tipocompra;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public BigDecimal getValoriva() {
        return valoriva;
    }

    public void setValoriva(BigDecimal valoriva) {
        this.valoriva = valoriva;
    }

    public BigDecimal getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(BigDecimal impuesto) {
        this.impuesto = impuesto;
    }

    public Boolean getCuatrimestre1() {
        return cuatrimestre1;
    }

    public void setCuatrimestre1(Boolean cuatrimestre1) {
        this.cuatrimestre1 = cuatrimestre1;
    }

    public Boolean getCuatrimestre2() {
        return cuatrimestre2;
    }

    public void setCuatrimestre2(Boolean cuatrimestre2) {
        this.cuatrimestre2 = cuatrimestre2;
    }

    public Boolean getCuatrimestre3() {
        return cuatrimestre3;
    }

    public void setCuatrimestre3(Boolean cuatrimestre3) {
        this.cuatrimestre3 = cuatrimestre3;
    }

    public String getTipoproducto() {
        return tipoproducto;
    }

    public void setTipoproducto(String tipoproducto) {
        this.tipoproducto = tipoproducto;
    }

    public Boolean getCatalogoelectronico() {
        return catalogoelectronico;
    }

    public void setCatalogoelectronico(Boolean catalogoelectronico) {
        this.catalogoelectronico = catalogoelectronico;
    }

    public String getProcedimientocontratacion() {
        return procedimientocontratacion;
    }

    public void setProcedimientocontratacion(String procedimientocontratacion) {
        this.procedimientocontratacion = procedimientocontratacion;
    }

    public Boolean getFondobid() {
        return fondobid;
    }

    public void setFondobid(Boolean fondobid) {
        this.fondobid = fondobid;
    }

    public String getNumerooperacion() {
        return numerooperacion;
    }

    public void setNumerooperacion(String numerooperacion) {
        this.numerooperacion = numerooperacion;
    }

    public String getCodigooperacion() {
        return codigooperacion;
    }

    public void setCodigooperacion(String codigooperacion) {
        this.codigooperacion = codigooperacion;
    }

    public String getRegimen() {
        return regimen;
    }

    public void setRegimen(String regimen) {
        this.regimen = regimen;
    }

    public String getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(String presupuesto) {
        this.presupuesto = presupuesto;
    }

    public Date getFechautilizacion() {
        return fechautilizacion;
    }

    public void setFechautilizacion(Date fechautilizacion) {
        this.fechautilizacion = fechautilizacion;
    }

    @XmlTransient
    public List<Proyectospoa> getProyectospoaList() {
        return proyectospoaList;
    }

    public void setProyectospoaList(List<Proyectospoa> proyectospoaList) {
        this.proyectospoaList = proyectospoaList;
    }

    public Proyectospoa getPadre() {
        return padre;
    }

    public void setPadre(Proyectospoa padre) {
        this.padre = padre;
    }

    @XmlTransient
    public List<Documentospoa> getDocumentospoaList() {
        return documentospoaList;
    }

    public void setDocumentospoaList(List<Documentospoa> documentospoaList) {
        this.documentospoaList = documentospoaList;
    }

    @XmlTransient
    public List<Trackingspoa> getTrackingspoaList() {
        return trackingspoaList;
    }

    public void setTrackingspoaList(List<Trackingspoa> trackingspoaList) {
        this.trackingspoaList = trackingspoaList;
    }

    @XmlTransient
    public List<Asignacionespoa> getAsignacionespoaList() {
        return asignacionespoaList;
    }

    public void setAsignacionespoaList(List<Asignacionespoa> asignacionespoaList) {
        this.asignacionespoaList = asignacionespoaList;
    }

    @XmlTransient
    public List<Ejecucionpoa> getEjecucionpoaList() {
        return ejecucionpoaList;
    }

    public void setEjecucionpoaList(List<Ejecucionpoa> ejecucionpoaList) {
        this.ejecucionpoaList = ejecucionpoaList;
    }

    @XmlTransient
    public List<Flujodecajapoa> getFlujodecajapoaList() {
        return flujodecajapoaList;
    }

    public void setFlujodecajapoaList(List<Flujodecajapoa> flujodecajapoaList) {
        this.flujodecajapoaList = flujodecajapoaList;
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
        if (!(object instanceof Proyectospoa)) {
            return false;
        }
        Proyectospoa other = (Proyectospoa) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre;
    }
    public String toStringNombre() {
        return nombre;
    }

    /**
     * @return the cuat1Str
     */
    public String getCuat1Str() {
        return cuat1Str;
    }

    /**
     * @param cuat1Str the cuat1Str to set
     */
    public void setCuat1Str(String cuat1Str) {
        this.cuat1Str = cuat1Str;
    }

    /**
     * @return the cuat2Str
     */
    public String getCuat2Str() {
        return cuat2Str;
    }

    /**
     * @param cuat2Str the cuat2Str to set
     */
    public void setCuat2Str(String cuat2Str) {
        this.cuat2Str = cuat2Str;
    }

    /**
     * @return the cuat3Str
     */
    public String getCuat3Str() {
        return cuat3Str;
    }

    /**
     * @param cuat3Str the cuat3Str to set
     */
    public void setCuat3Str(String cuat3Str) {
        this.cuat3Str = cuat3Str;
    }

    public Boolean getPac() {
        return pac;
    }

    public void setPac(Boolean pac) {
        this.pac = pac;
    }

    public Date getFechapac() {
        return fechapac;
    }

    public void setFechapac(Date fechapac) {
        this.fechapac = fechapac;
    }

    public Date getFechacreacion() {
        return fechacreacion;
    }

    public void setFechacreacion(Date fechacreacion) {
        this.fechacreacion = fechacreacion;
    }

    public Date getFechareforma() {
        return fechareforma;
    }

    public void setFechareforma(Date fechareforma) {
        this.fechareforma = fechareforma;
    }

    public String getModificacion() {
        return modificacion;
    }

    public void setModificacion(String modificacion) {
        this.modificacion = modificacion;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    /**
     * @return the partidaStr
     */
    public String getPartidaStr() {
        return partidaStr;
    }

    /**
     * @param partidaStr the partidaStr to set
     */
    public void setPartidaStr(String partidaStr) {
        this.partidaStr = partidaStr;
    }

    @XmlTransient
    public List<Trackingproyectospoa> getTrackingproyectospoaList() {
        return trackingproyectospoaList;
    }

    public void setTrackingproyectospoaList(List<Trackingproyectospoa> trackingproyectospoaList) {
        this.trackingproyectospoaList = trackingproyectospoaList;
    }

    public String getDireccionant() {
        return direccionant;
    }

    public void setDireccionant(String direccionant) {
        this.direccionant = direccionant;
    }

    public Integer getReformapac() {
        return reformapac;
    }

    public void setReformapac(Integer reformapac) {
        this.reformapac = reformapac;
    }

    @XmlTransient
    public List<Reformaspoa> getReformaspoaList() {
        return reformaspoaList;
    }

    public void setReformaspoaList(List<Reformaspoa> reformaspoaList) {
        this.reformaspoaList = reformaspoaList;
    }
}