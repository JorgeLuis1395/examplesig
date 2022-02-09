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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "lotessuministros")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Lotessuministros.findAll", query = "SELECT l FROM Lotessuministros l")
    , @NamedQuery(name = "Lotessuministros.findById", query = "SELECT l FROM Lotessuministros l WHERE l.id = :id")
    , @NamedQuery(name = "Lotessuministros.findByLote", query = "SELECT l FROM Lotessuministros l WHERE l.lote = :lote")
    , @NamedQuery(name = "Lotessuministros.findByCantidad", query = "SELECT l FROM Lotessuministros l WHERE l.cantidad = :cantidad")
    , @NamedQuery(name = "Lotessuministros.findByFechaCaduca", query = "SELECT l FROM Lotessuministros l WHERE l.fechaCaduca = :fechaCaduca")})
public class Lotessuministros implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "lote")
    private String lote;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "cantidad")
    private BigDecimal cantidad;
    @Column(name = "fecha_caduca")
    @Temporal(TemporalType.DATE)
    private Date fechaCaduca;
    @JoinColumn(name = "suministro", referencedColumnName = "id")
    @ManyToOne
    private Suministros suministro;

    public Lotessuministros() {
    }

    public Lotessuministros(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public Date getFechaCaduca() {
        return fechaCaduca;
    }

    public void setFechaCaduca(Date fechaCaduca) {
        this.fechaCaduca = fechaCaduca;
    }

    public Suministros getSuministro() {
        return suministro;
    }

    public void setSuministro(Suministros suministro) {
        this.suministro = suministro;
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
        if (!(object instanceof Lotessuministros)) {
            return false;
        }
        Lotessuministros other = (Lotessuministros) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return lote;
    }

}