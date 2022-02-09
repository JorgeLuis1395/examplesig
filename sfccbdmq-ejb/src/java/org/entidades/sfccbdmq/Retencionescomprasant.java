/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "retencionescomprasant")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Retencionescomprasant.findAll", query = "SELECT r FROM Retencionescomprasant r")
    , @NamedQuery(name = "Retencionescomprasant.findByObligacion", query = "SELECT r FROM Retencionescomprasant r WHERE r.obligacion = :obligacion")
    , @NamedQuery(name = "Retencionescomprasant.findByRetencion", query = "SELECT r FROM Retencionescomprasant r WHERE r.retencion = :retencion")
    , @NamedQuery(name = "Retencionescomprasant.findByBaseimponible", query = "SELECT r FROM Retencionescomprasant r WHERE r.baseimponible = :baseimponible")
    , @NamedQuery(name = "Retencionescomprasant.findByImpuesto", query = "SELECT r FROM Retencionescomprasant r WHERE r.impuesto = :impuesto")
    , @NamedQuery(name = "Retencionescomprasant.findByValor", query = "SELECT r FROM Retencionescomprasant r WHERE r.valor = :valor")
    , @NamedQuery(name = "Retencionescomprasant.findByBien", query = "SELECT r FROM Retencionescomprasant r WHERE r.bien = :bien")
    , @NamedQuery(name = "Retencionescomprasant.findByRetencionimpuesto", query = "SELECT r FROM Retencionescomprasant r WHERE r.retencionimpuesto = :retencionimpuesto")
    , @NamedQuery(name = "Retencionescomprasant.findByPartida", query = "SELECT r FROM Retencionescomprasant r WHERE r.partida = :partida")
    , @NamedQuery(name = "Retencionescomprasant.findById", query = "SELECT r FROM Retencionescomprasant r WHERE r.id = :id")})
public class Retencionescomprasant implements Serializable {

    private static final long serialVersionUID = 1L;
    @Column(name = "obligacion")
    private Integer obligacion;
    @Column(name = "retencion")
    private Integer retencion;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "baseimponible")
    private BigDecimal baseimponible;
    @Column(name = "impuesto")
    private Integer impuesto;
    @Column(name = "valor")
    private BigDecimal valor;
    @Column(name = "bien")
    private Boolean bien;
    @Column(name = "retencionimpuesto")
    private Boolean retencionimpuesto;
    @Size(max = 2147483647)
    @Column(name = "partida")
    private String partida;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Integer id;

    public Retencionescomprasant() {
    }

    public Retencionescomprasant(Integer id) {
        this.id = id;
    }

    public Integer getObligacion() {
        return obligacion;
    }

    public void setObligacion(Integer obligacion) {
        this.obligacion = obligacion;
    }

    public Integer getRetencion() {
        return retencion;
    }

    public void setRetencion(Integer retencion) {
        this.retencion = retencion;
    }

    public BigDecimal getBaseimponible() {
        return baseimponible;
    }

    public void setBaseimponible(BigDecimal baseimponible) {
        this.baseimponible = baseimponible;
    }

    public Integer getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(Integer impuesto) {
        this.impuesto = impuesto;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Boolean getBien() {
        return bien;
    }

    public void setBien(Boolean bien) {
        this.bien = bien;
    }

    public Boolean getRetencionimpuesto() {
        return retencionimpuesto;
    }

    public void setRetencionimpuesto(Boolean retencionimpuesto) {
        this.retencionimpuesto = retencionimpuesto;
    }

    public String getPartida() {
        return partida;
    }

    public void setPartida(String partida) {
        this.partida = partida;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
        if (!(object instanceof Retencionescomprasant)) {
            return false;
        }
        Retencionescomprasant other = (Retencionescomprasant) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.sfccbdmq.Retencionescomprasant[ id=" + id + " ]";
    }
    
}
