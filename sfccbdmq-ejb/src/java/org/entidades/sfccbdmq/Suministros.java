/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
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
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "suministros")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Suministros.findAll", query = "SELECT s FROM Suministros s"),
    @NamedQuery(name = "Suministros.findById", query = "SELECT s FROM Suministros s WHERE s.id = :id"),
    @NamedQuery(name = "Suministros.findByAlias", query = "SELECT s FROM Suministros s WHERE s.alias = :alias"),
    @NamedQuery(name = "Suministros.findByCodigobarras", query = "SELECT s FROM Suministros s WHERE s.codigobarras = :codigobarras"),
    @NamedQuery(name = "Suministros.findByNombre", query = "SELECT s FROM Suministros s WHERE s.nombre = :nombre"),
    @NamedQuery(name = "Suministros.findByComercial", query = "SELECT s FROM Suministros s WHERE s.comercial = :comercial"),
    @NamedQuery(name = "Suministros.findByFechacreacion", query = "SELECT s FROM Suministros s WHERE s.fechacreacion = :fechacreacion"),
    @NamedQuery(name = "Suministros.findByObservaciones", query = "SELECT s FROM Suministros s WHERE s.observaciones = :observaciones"),
    @NamedQuery(name = "Suministros.findByEstado", query = "SELECT s FROM Suministros s WHERE s.estado = :estado"),
    @NamedQuery(name = "Suministros.findByRango", query = "SELECT s FROM Suministros s WHERE s.rango = :rango"),
    @NamedQuery(name = "Suministros.findByPvp", query = "SELECT s FROM Suministros s WHERE s.pvp = :pvp")})
public class Suministros implements Serializable {

    @Column(name = "lote")
    private Boolean lote;
    @Column(name = "serie")
    private Boolean serie;

    @OneToMany(mappedBy = "suministro")
    private List<Lotessuministros> lotessuministrosList;

    @OneToMany(mappedBy = "suministro")
    private List<Adicionalessuministro> adicionalessuministroList;
    @OneToMany(mappedBy = "suministro")
    private List<Tomasuministro> tomasuministroList;
    @OneToMany(mappedBy = "suministro")
    private List<Comprasuministros> comprasuministrosList;
    @OneToMany(mappedBy = "suministro")
    private List<Empleadosuministros> empleadosuministrosList;
    @OneToMany(mappedBy = "suministro")
    private List<Detalleorden> detalleordenList;
    @OneToMany(mappedBy = "suministro")
    private List<Bodegasuministro> bodegasuministroList;
    @OneToMany(mappedBy = "suministro")
    private List<Kardexinventario> kardexinventarioList;
    @OneToMany(mappedBy = "suministro")
    private List<Organigramasuministros> organigramasuministrosList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "alias")
    private String alias;
    @Size(max = 2147483647)
    @Column(name = "codigobarras")
    private String codigobarras;
    @Size(max = 2147483647)
    @Column(name = "nombre")
    private String nombre;
    @Size(max = 2147483647)
    @Column(name = "comercial")
    private String comercial;
    @Column(name = "fechacreacion")
    @Temporal(TemporalType.DATE)
    private Date fechacreacion;
    @Size(max = 2147483647)
    @Column(name = "observaciones")
    private String observaciones;
    @Column(name = "estado")
    private Integer estado;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "rango")
    private Float rango;
    @Column(name = "pvp")
    private Float pvp;
    @OneToMany(mappedBy = "suministro")
    private List<Detallesolicitud> detallesolicitudList;
    @JoinColumn(name = "unidad", referencedColumnName = "id")
    @ManyToOne
    private Unidades unidad;
    @JoinColumn(name = "tipo", referencedColumnName = "id")
    @ManyToOne
    private Tiposuministros tipo;
    @JoinColumn(name = "impuesto", referencedColumnName = "id")
    @ManyToOne
    private Impuestos impuesto;
    @Transient
    private double cantidad;
    @Transient
    private double cantidadinv;
    @Transient
    private double costopr;
    @Transient
    private double costoprinve;
    @Transient
    private double cantidadbodega;
    @Transient
    private double cantidadbodinv;
    @Transient
    private double requerido;
    @Transient
    private double requeridoinv;
    @Transient
    private double totalRequerido;
    @Transient
    private String codigoFamilia;
    @Transient
    private String codigoSubFamilia;
    @Transient
    private double valorImpuesto;
    @Transient
    private Boolean mostrar;
    public Suministros() {
    }

    public Suministros(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getCodigobarras() {
        return codigobarras;
    }

    public void setCodigobarras(String codigobarras) {
        this.codigobarras = codigobarras;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getComercial() {
        return comercial;
    }

    public void setComercial(String comercial) {
        this.comercial = comercial;
    }

    public Date getFechacreacion() {
        return fechacreacion;
    }

    public void setFechacreacion(Date fechacreacion) {
        this.fechacreacion = fechacreacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Float getRango() {
        return rango;
    }

    public void setRango(Float rango) {
        this.rango = rango;
    }

    public Float getPvp() {
        return pvp;
    }

    public void setPvp(Float pvp) {
        this.pvp = pvp;
    }

    @XmlTransient
    public List<Detallesolicitud> getDetallesolicitudList() {
        return detallesolicitudList;
    }

    public void setDetallesolicitudList(List<Detallesolicitud> detallesolicitudList) {
        this.detallesolicitudList = detallesolicitudList;
    }

    public Unidades getUnidad() {
        return unidad;
    }

    public void setUnidad(Unidades unidad) {
        this.unidad = unidad;
    }

    public Tiposuministros getTipo() {
        return tipo;
    }

    public void setTipo(Tiposuministros tipo) {
        this.tipo = tipo;
    }

    public Impuestos getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(Impuestos impuesto) {
        this.impuesto = impuesto;
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
        if (!(object instanceof Suministros)) {
            return false;
        }
        Suministros other = (Suministros) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return codigobarras+" - "+nombre;
    }

    @XmlTransient
    public List<Organigramasuministros> getOrganigramasuministrosList() {
        return organigramasuministrosList;
    }

    public void setOrganigramasuministrosList(List<Organigramasuministros> organigramasuministrosList) {
        this.organigramasuministrosList = organigramasuministrosList;
    }

    /**
     * @return the cantidad
     */
    public double getCantidad() {
        return cantidad;
    }

    /**
     * @param cantidad the cantidad to set
     */
    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    @XmlTransient
    public List<Kardexinventario> getKardexinventarioList() {
        return kardexinventarioList;
    }

    public void setKardexinventarioList(List<Kardexinventario> kardexinventarioList) {
        this.kardexinventarioList = kardexinventarioList;
    }

    @XmlTransient
    public List<Bodegasuministro> getBodegasuministroList() {
        return bodegasuministroList;
    }

    public void setBodegasuministroList(List<Bodegasuministro> bodegasuministroList) {
        this.bodegasuministroList = bodegasuministroList;
    }

    @XmlTransient
    public List<Empleadosuministros> getEmpleadosuministrosList() {
        return empleadosuministrosList;
    }

    public void setEmpleadosuministrosList(List<Empleadosuministros> empleadosuministrosList) {
        this.empleadosuministrosList = empleadosuministrosList;
    }

    @XmlTransient
    public List<Detalleorden> getDetalleordenList() {
        return detalleordenList;
    }

    public void setDetalleordenList(List<Detalleorden> detalleordenList) {
        this.detalleordenList = detalleordenList;
    }

    /**
     * @return the cantidadinv
     */
    public double getCantidadinv() {
        return cantidadinv;
    }

    /**
     * @param cantidadinv the cantidadinv to set
     */
    public void setCantidadinv(double cantidadinv) {
        this.cantidadinv = cantidadinv;
    }

    /**
     * @return the cantidadbodega
     */
    public double getCantidadbodega() {
        return cantidadbodega;
    }

    /**
     * @param cantidadbodega the cantidadbodega to set
     */
    public void setCantidadbodega(double cantidadbodega) {
        this.cantidadbodega = cantidadbodega;
    }

    /**
     * @return the cantidadbodinv
     */
    public double getCantidadbodinv() {
        return cantidadbodinv;
    }

    /**
     * @param cantidadbodinv the cantidadbodinv to set
     */
    public void setCantidadbodinv(double cantidadbodinv) {
        this.cantidadbodinv = cantidadbodinv;
    }

    /**
     * @return the requerido
     */
    public double getRequerido() {
        return requerido;
    }

    /**
     * @param requerido the requerido to set
     */
    public void setRequerido(double requerido) {
        this.requerido = requerido;
    }

    /**
     * @return the requeridoinv
     */
    public double getRequeridoinv() {
        return requeridoinv;
    }

    /**
     * @param requeridoinv the requeridoinv to set
     */
    public void setRequeridoinv(double requeridoinv) {
        this.requeridoinv = requeridoinv;
    }

    /**
     * @return the totalRequerido
     */
    public double getTotalRequerido() {
        return totalRequerido;
    }

    /**
     * @param totalRequerido the totalRequerido to set
     */
    public void setTotalRequerido(double totalRequerido) {
        this.totalRequerido = totalRequerido;
    }

    @XmlTransient
    public List<Comprasuministros> getComprasuministrosList() {
        return comprasuministrosList;
    }

    public void setComprasuministrosList(List<Comprasuministros> comprasuministrosList) {
        this.comprasuministrosList = comprasuministrosList;
    }

    @XmlTransient
    public List<Adicionalessuministro> getAdicionalessuministroList() {
        return adicionalessuministroList;
    }

    public void setAdicionalessuministroList(List<Adicionalessuministro> adicionalessuministroList) {
        this.adicionalessuministroList = adicionalessuministroList;
    }

    @XmlTransient
    public List<Tomasuministro> getTomasuministroList() {
        return tomasuministroList;
    }

    public void setTomasuministroList(List<Tomasuministro> tomasuministroList) {
        this.tomasuministroList = tomasuministroList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Lotessuministros> getLotessuministrosList() {
        return lotessuministrosList;
    }

    public void setLotessuministrosList(List<Lotessuministros> lotessuministrosList) {
        this.lotessuministrosList = lotessuministrosList;
    }

    /**
     * @return the codigoFamilia
     */
    public String getCodigoFamilia() {
        return codigoFamilia;
    }

    /**
     * @param codigoFamilia the codigoFamilia to set
     */
    public void setCodigoFamilia(String codigoFamilia) {
        this.codigoFamilia = codigoFamilia;
    }

    /**
     * @return the codigoSubFamilia
     */
    public String getCodigoSubFamilia() {
        return codigoSubFamilia;
    }

    /**
     * @param codigoSubFamilia the codigoSubFamilia to set
     */
    public void setCodigoSubFamilia(String codigoSubFamilia) {
        this.codigoSubFamilia = codigoSubFamilia;
    }

    /**
     * @return the valorImpuesto
     */
    public double getValorImpuesto() {
        return valorImpuesto;
    }

    /**
     * @param valorImpuesto the valorImpuesto to set
     */
    public void setValorImpuesto(double valorImpuesto) {
        this.valorImpuesto = valorImpuesto;
    }

    public Boolean getLote() {
        return lote;
    }

    public void setLote(Boolean lote) {
        this.lote = lote;
    }

    public Boolean getSerie() {
        return serie;
    }

    public void setSerie(Boolean serie) {
        this.serie = serie;
    }

    /**
     * @return the costopr
     */
    public double getCostopr() {
        return costopr;
    }

    /**
     * @param costopr the costopr to set
     */
    public void setCostopr(double costopr) {
        this.costopr = costopr;
    }

    /**
     * @return the costoprinve
     */
    public double getCostoprinve() {
        return costoprinve;
    }

    /**
     * @param costoprinve the costoprinve to set
     */
    public void setCostoprinve(double costoprinve) {
        this.costoprinve = costoprinve;
    }

    /**
     * @return the mostrar
     */
    public Boolean getMostrar() {
        return mostrar;
    }

    /**
     * @param mostrar the mostrar to set
     */
    public void setMostrar(Boolean mostrar) {
        this.mostrar = mostrar;
    }
    
}