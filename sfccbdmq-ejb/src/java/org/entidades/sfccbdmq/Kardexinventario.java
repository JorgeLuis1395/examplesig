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
import javax.persistence.Cacheable;
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
@Cacheable(false)
@Table(name = "kardexinventario")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Kardexinventario.findAll", query = "SELECT k FROM Kardexinventario k"),
    @NamedQuery(name = "Kardexinventario.findById", query = "SELECT k FROM Kardexinventario k WHERE k.id = :id"),
    @NamedQuery(name = "Kardexinventario.findByFecha", query = "SELECT k FROM Kardexinventario k WHERE k.fecha = :fecha"),
    @NamedQuery(name = "Kardexinventario.findByHora", query = "SELECT k FROM Kardexinventario k WHERE k.hora = :hora"),
    @NamedQuery(name = "Kardexinventario.findByCantidad", query = "SELECT k FROM Kardexinventario k WHERE k.cantidad = :cantidad"),
    @NamedQuery(name = "Kardexinventario.findByCosto", query = "SELECT k FROM Kardexinventario k WHERE k.costo = :costo"),
    @NamedQuery(name = "Kardexinventario.findByCostopromedio", query = "SELECT k FROM Kardexinventario k WHERE k.costopromedio = :costopromedio"),
    @NamedQuery(name = "Kardexinventario.findBySaldoanterior", query = "SELECT k FROM Kardexinventario k WHERE k.saldoanterior = :saldoanterior")})
public class Kardexinventario implements Serializable {

    @Column(name = "tipoorden")
    private Integer tipoorden;

    @Column(name = "numero")
    private Integer numero;

    /**
     * @return the nombreLote
     */
    public String getNombreLote() {
        return nombreLote;
    }

    /**
     * @param nombreLote the nombreLote to set
     */
    public void setNombreLote(String nombreLote) {
        this.nombreLote = nombreLote;
    }

    /**
     * @return the fechaCaduca
     */
    public Date getFechaCaduca() {
        return fechaCaduca;
    }

    /**
     * @param fechaCaduca the fechaCaduca to set
     */
    public void setFechaCaduca(Date fechaCaduca) {
        this.fechaCaduca = fechaCaduca;
    }

    @Size(max = 2147483647)
    @Column(name = "serie")
    private String serie;
    @JoinColumn(name = "lote", referencedColumnName = "id")
    @ManyToOne
    private Lotessuministros lote;

   
    @Column(name = "costopinversion")
    private Float costopinversion;
    @Column(name = "cantidadinversion")
    private Float cantidadinversion;
    @Column(name = "saldoanteriorinversion")
    private Float saldoanteriorinversion;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "signo")
    private Integer signo;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Column(name = "hora")
    @Temporal(TemporalType.TIME)
    private Date hora;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "cantidad")
    private Float cantidad;
    @Column(name = "costo")
    private Float costo;
    @Column(name = "observaciones")
    private String observaciones;
    @Column(name = "costopromedio")
    private Float costopromedio;
    @Column(name = "saldoanterior")
    private Float saldoanterior;
    @OneToMany(mappedBy = "kardex")
    private List<Adicionalkardex> adicionalkardexList;
    @JoinColumn(name = "unidad", referencedColumnName = "id")
    @ManyToOne
    private Unidades unidad;
    @JoinColumn(name = "suministro", referencedColumnName = "id")
    @ManyToOne
    private Suministros suministro;
    @JoinColumn(name = "cabecerainventario", referencedColumnName = "id")
    @ManyToOne
    private Cabecerainventario cabecerainventario;
    @JoinColumn(name = "bodega", referencedColumnName = "id")
    @ManyToOne
    private Bodegas bodega;
    @Transient
    private double canTotal;
    @Transient
    private double total;
    @Transient
    private String codigoSuministro;
    @Transient
    private String nombreLote;
    @Transient
    private Date fechaCaduca;
    public Kardexinventario() {
    }

    public Kardexinventario(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Date getHora() {
        return hora;
    }

    public void setHora(Date hora) {
        this.hora = hora;
    }

    public Float getCantidad() {
        return cantidad;
    }

    public void setCantidad(Float cantidad) {
        this.cantidad = cantidad;
    }

    public Float getCosto() {
        return costo;
    }

    public void setCosto(Float costo) {
        this.costo = costo;
    }

    public Float getCostopromedio() {
        return costopromedio;
    }

    public void setCostopromedio(Float costopromedio) {
        this.costopromedio = costopromedio;
    }

    public Float getSaldoanterior() {
        return saldoanterior;
    }

    public void setSaldoanterior(Float saldoanterior) {
        this.saldoanterior = saldoanterior;
    }

    @XmlTransient
    public List<Adicionalkardex> getAdicionalkardexList() {
        return adicionalkardexList;
    }

    public void setAdicionalkardexList(List<Adicionalkardex> adicionalkardexList) {
        this.adicionalkardexList = adicionalkardexList;
    }

    public Unidades getUnidad() {
        return unidad;
    }

    public void setUnidad(Unidades unidad) {
        this.unidad = unidad;
    }

    public Suministros getSuministro() {
        return suministro;
    }

    public void setSuministro(Suministros suministro) {
        this.suministro = suministro;
    }

    public Cabecerainventario getCabecerainventario() {
        return cabecerainventario;
    }

    public void setCabecerainventario(Cabecerainventario cabecerainventario) {
        this.cabecerainventario = cabecerainventario;
    }

    public Bodegas getBodega() {
        return bodega;
    }

    public void setBodega(Bodegas bodega) {
        this.bodega = bodega;
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
        if (!(object instanceof Kardexinventario)) {
            return false;
        }
        Kardexinventario other = (Kardexinventario) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Kardexinventario[ id=" + id + " ]";
    }

    /**
     * @return the observaciones
     */
    public String getObservaciones() {
        return observaciones;
    }

    /**
     * @param observaciones the observaciones to set
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Float getCantidadinversion() {
        return cantidadinversion;
    }

    public void setCantidadinversion(Float cantidadinversion) {
        this.cantidadinversion = cantidadinversion;
    }

    public Float getSaldoanteriorinversion() {
        return saldoanteriorinversion;
    }

    public void setSaldoanteriorinversion(Float saldoanteriorinversion) {
        this.saldoanteriorinversion = saldoanteriorinversion;
    }

    /**
     * @return the signo
     */
    public Integer getSigno() {
        return signo;
    }

    /**
     * @param signo the signo to set
     */
    public void setSigno(Integer signo) {
        this.signo = signo;
    }

    /**
     * @return the canTotal
     */
    public double getCanTotal() {
        return canTotal;
    }

    /**
     * @param canTotal the canTotal to set
     */
    public void setCanTotal(double canTotal) {
        this.canTotal = canTotal;
    }

    /**
     * @return the total
     */
    public double getTotal() {
        return total;
    }

    /**
     * @param total the total to set
     */
    public void setTotal(double total) {
        this.total = total;
    }

    /**
     * @return the costopinversion
     */
    public Float getCostopinversion() {
        return costopinversion;
    }

    /**
     * @param costopinversion the costopinversion to set
     */
    public void setCostopinversion(Float costopinversion) {
        this.costopinversion = costopinversion;
    }

    /**
     * @return the codigoSuministro
     */
    public String getCodigoSuministro() {
        return codigoSuministro;
    }

    /**
     * @param codigoSuministro the codigoSuministro to set
     */
    public void setCodigoSuministro(String codigoSuministro) {
        this.codigoSuministro = codigoSuministro;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public Lotessuministros getLote() {
        return lote;
    }

    public void setLote(Lotessuministros lote) {
        this.lote = lote;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Integer getTipoorden() {
        return tipoorden;
    }

    public void setTipoorden(Integer tipoorden) {
        this.tipoorden = tipoorden;
    }

    

}