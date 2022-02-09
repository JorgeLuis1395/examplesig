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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "equivalenciasev")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Equivalenciasev.findAll", query = "SELECT e FROM Equivalenciasev e"),
    @NamedQuery(name = "Equivalenciasev.findById", query = "SELECT e FROM Equivalenciasev e WHERE e.id = :id"),
    @NamedQuery(name = "Equivalenciasev.findByNotaminima", query = "SELECT e FROM Equivalenciasev e WHERE e.notaminima = :notaminima"),
    @NamedQuery(name = "Equivalenciasev.findByNotamaxima", query = "SELECT e FROM Equivalenciasev e WHERE e.notamaxima = :notamaxima"),
    @NamedQuery(name = "Equivalenciasev.findByPuntajefijo", query = "SELECT e FROM Equivalenciasev e WHERE e.puntajefijo = :puntajefijo"),
    @NamedQuery(name = "Equivalenciasev.findByDescripcion", query = "SELECT e FROM Equivalenciasev e WHERE e.descripcion = :descripcion"),
    @NamedQuery(name = "Equivalenciasev.findByActivo", query = "SELECT e FROM Equivalenciasev e WHERE e.activo = :activo")})
public class Equivalenciasev implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "notaminima")
    private BigDecimal notaminima;
    @Column(name = "notamaxima")
    private BigDecimal notamaxima;
    @Column(name = "puntajefijo")
    private BigDecimal puntajefijo;
    @Size(max = 2147483647)
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "activo")
    private Boolean activo;

    public Equivalenciasev() {
    }

    public Equivalenciasev(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getNotaminima() {
        return notaminima;
    }

    public void setNotaminima(BigDecimal notaminima) {
        this.notaminima = notaminima;
    }

    public BigDecimal getNotamaxima() {
        return notamaxima;
    }

    public void setNotamaxima(BigDecimal notamaxima) {
        this.notamaxima = notamaxima;
    }

    public BigDecimal getPuntajefijo() {
        return puntajefijo;
    }

    public void setPuntajefijo(BigDecimal puntajefijo) {
        this.puntajefijo = puntajefijo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
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
        if (!(object instanceof Equivalenciasev)) {
            return false;
        }
        Equivalenciasev other = (Equivalenciasev) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Equivalenciasev[ id=" + id + " ]";
    }
    
}
