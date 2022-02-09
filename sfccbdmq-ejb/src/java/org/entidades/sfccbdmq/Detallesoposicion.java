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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "detallesoposicion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Detallesoposicion.findAll", query = "SELECT d FROM Detallesoposicion d"),
    @NamedQuery(name = "Detallesoposicion.findById", query = "SELECT d FROM Detallesoposicion d WHERE d.id = :id"),
    @NamedQuery(name = "Detallesoposicion.findByNombre", query = "SELECT d FROM Detallesoposicion d WHERE d.nombre = :nombre"),
    @NamedQuery(name = "Detallesoposicion.findByNotamaxima", query = "SELECT d FROM Detallesoposicion d WHERE d.notamaxima = :notamaxima"),
    @NamedQuery(name = "Detallesoposicion.findByDescripcion", query = "SELECT d FROM Detallesoposicion d WHERE d.descripcion = :descripcion"),
    @NamedQuery(name = "Detallesoposicion.findByActivo", query = "SELECT d FROM Detallesoposicion d WHERE d.activo = :activo"),
    @NamedQuery(name = "Detallesoposicion.findByOrden", query = "SELECT d FROM Detallesoposicion d WHERE d.orden = :orden")})
public class Detallesoposicion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "nombre")
    private String nombre;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "notamaxima")
    private BigDecimal notamaxima;
    @Size(max = 2147483647)
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "activo")
    private Boolean activo;
    @Column(name = "orden")
    private Integer orden;
    @JoinColumn(name = "area", referencedColumnName = "id")
    @ManyToOne
    private Areasseleccion area;
    @OneToMany(mappedBy = "detalle")
    private List<Calificacionesoposicion> calificacionesoposicionList;

    public Detallesoposicion() {
    }

    public Detallesoposicion(Integer id) {
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

    public BigDecimal getNotamaxima() {
        return notamaxima;
    }

    public void setNotamaxima(BigDecimal notamaxima) {
        this.notamaxima = notamaxima;
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

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public Areasseleccion getArea() {
        return area;
    }

    public void setArea(Areasseleccion area) {
        this.area = area;
    }

    @XmlTransient
    public List<Calificacionesoposicion> getCalificacionesoposicionList() {
        return calificacionesoposicionList;
    }

    public void setCalificacionesoposicionList(List<Calificacionesoposicion> calificacionesoposicionList) {
        this.calificacionesoposicionList = calificacionesoposicionList;
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
        if (!(object instanceof Detallesoposicion)) {
            return false;
        }
        Detallesoposicion other = (Detallesoposicion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Detallesoposicion[ id=" + id + " ]";
    }
    
}
