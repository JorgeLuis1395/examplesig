/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "tiposseleccion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tiposseleccion.findAll", query = "SELECT t FROM Tiposseleccion t"),
    @NamedQuery(name = "Tiposseleccion.findById", query = "SELECT t FROM Tiposseleccion t WHERE t.id = :id"),
    @NamedQuery(name = "Tiposseleccion.findByNombre", query = "SELECT t FROM Tiposseleccion t WHERE t.nombre = :nombre"),
    @NamedQuery(name = "Tiposseleccion.findByActivo", query = "SELECT t FROM Tiposseleccion t WHERE t.activo = :activo"),
    @NamedQuery(name = "Tiposseleccion.findByEstatus", query = "SELECT t FROM Tiposseleccion t WHERE t.estatus = :estatus"),
    @NamedQuery(name = "Tiposseleccion.findByPonderacion", query = "SELECT t FROM Tiposseleccion t WHERE t.ponderacion = :ponderacion"),
    @NamedQuery(name = "Tiposseleccion.findByNotamaxima", query = "SELECT t FROM Tiposseleccion t WHERE t.notamaxima = :notamaxima"),
    @NamedQuery(name = "Tiposseleccion.findByOrden", query = "SELECT t FROM Tiposseleccion t WHERE t.orden = :orden"),
    @NamedQuery(name = "Tiposseleccion.findByNotaminima", query = "SELECT t FROM Tiposseleccion t WHERE t.notaminima = :notaminima")})
public class Tiposseleccion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "activo")
    private Boolean activo;
    @Column(name = "estatus")
    private Boolean estatus;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "ponderacion")
    private BigDecimal ponderacion;
    @Column(name = "notamaxima")
    private BigDecimal notamaxima;
    @Column(name = "orden")
    private Integer orden;
    @Column(name = "notaminima")
    private BigDecimal notaminima;
    @OneToMany(mappedBy = "tipo")
    private List<Areasseleccion> areasseleccionList;

    public Tiposseleccion() {
    }

    public Tiposseleccion(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Boolean getEstatus() {
        return estatus;
    }

    public void setEstatus(Boolean estatus) {
        this.estatus = estatus;
    }

    public BigDecimal getPonderacion() {
        return ponderacion;
    }

    public void setPonderacion(BigDecimal ponderacion) {
        this.ponderacion = ponderacion;
    }

    public BigDecimal getNotamaxima() {
        return notamaxima;
    }

    public void setNotamaxima(BigDecimal notamaxima) {
        this.notamaxima = notamaxima;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public BigDecimal getNotaminima() {
        return notaminima;
    }

    public void setNotaminima(BigDecimal notaminima) {
        this.notaminima = notaminima;
    }

    @XmlTransient
    public List<Areasseleccion> getAreasseleccionList() {
        return areasseleccionList;
    }

    public void setAreasseleccionList(List<Areasseleccion> areasseleccionList) {
        this.areasseleccionList = areasseleccionList;
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
        if (!(object instanceof Tiposseleccion)) {
            return false;
        }
        Tiposseleccion other = (Tiposseleccion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nombre;
    }
    
}
