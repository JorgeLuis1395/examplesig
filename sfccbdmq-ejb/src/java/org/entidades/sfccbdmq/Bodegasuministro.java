/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@Entity
@Cacheable(false)
@Table(name = "bodegasuministro")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Bodegasuministro.findAll", query = "SELECT b FROM Bodegasuministro b"),
    @NamedQuery(name = "Bodegasuministro.findById", query = "SELECT b FROM Bodegasuministro b WHERE b.id = :id"),
    @NamedQuery(name = "Bodegasuministro.findBySaldo", query = "SELECT b FROM Bodegasuministro b WHERE b.saldo = :saldo"),
    @NamedQuery(name = "Bodegasuministro.findByCostopromedio", query = "SELECT b FROM Bodegasuministro b WHERE b.costopromedio = :costopromedio"),
    @NamedQuery(name = "Bodegasuministro.findByFecha", query = "SELECT b FROM Bodegasuministro b WHERE b.fecha = :fecha"),
    @NamedQuery(name = "Bodegasuministro.findByHora", query = "SELECT b FROM Bodegasuministro b WHERE b.hora = :hora")})
public class Bodegasuministro implements Serializable {
    @Column(name = "saldoinversion")
    private Float saldoinversion;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "saldo")
    private Float saldo;
    @Column(name = "costopromedio")
    private Float costopromedio;
    @Column(name = "costopromedioinversion")
    private Float costopromedioinversion;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Column(name = "hora")
    @Temporal(TemporalType.TIME)
    private Date hora;
    @JoinColumn(name = "unidad", referencedColumnName = "id")
    @ManyToOne
    private Unidades unidad;
    @JoinColumn(name = "suministro", referencedColumnName = "id")
    @ManyToOne
    private Suministros suministro;
    @JoinColumn(name = "bodega", referencedColumnName = "id")
    @ManyToOne
    private Bodegas bodega;

    public Bodegasuministro() {
    }

    public Bodegasuministro(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Float getSaldo() {
        return saldo;
    }

    public void setSaldo(Float saldo) {
        this.saldo = saldo;
    }

    public Float getCostopromedio() {
        return costopromedio;
    }

    public void setCostopromedio(Float costopromedio) {
        this.costopromedio = costopromedio;
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
        if (!(object instanceof Bodegasuministro)) {
            return false;
        }
        Bodegasuministro other = (Bodegasuministro) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Bodegasuministro[ id=" + id + " ]";
    }

    public Float getSaldoinversion() {
        return saldoinversion;
    }

    public void setSaldoinversion(Float saldoinversion) {
        this.saldoinversion = saldoinversion;
    }

    /**
     * @return the costopromedioinversion
     */
    public Float getCostopromedioinversion() {
        return costopromedioinversion;
    }

    /**
     * @param costopromedioinversion the costopromedioinversion to set
     */
    public void setCostopromedioinversion(Float costopromedioinversion) {
        this.costopromedioinversion = costopromedioinversion;
    }
    
}
