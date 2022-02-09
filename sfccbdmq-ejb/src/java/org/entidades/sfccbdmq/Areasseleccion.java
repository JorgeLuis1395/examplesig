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
@Table(name = "areasseleccion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Areasseleccion.findAll", query = "SELECT a FROM Areasseleccion a"),
    @NamedQuery(name = "Areasseleccion.findById", query = "SELECT a FROM Areasseleccion a WHERE a.id = :id"),
    @NamedQuery(name = "Areasseleccion.findByNombre", query = "SELECT a FROM Areasseleccion a WHERE a.nombre = :nombre"),
    @NamedQuery(name = "Areasseleccion.findByActivo", query = "SELECT a FROM Areasseleccion a WHERE a.activo = :activo"),
    @NamedQuery(name = "Areasseleccion.findByPonderacion", query = "SELECT a FROM Areasseleccion a WHERE a.ponderacion = :ponderacion"),
    @NamedQuery(name = "Areasseleccion.findByNotamaxima", query = "SELECT a FROM Areasseleccion a WHERE a.notamaxima = :notamaxima"),
    @NamedQuery(name = "Areasseleccion.findByOrden", query = "SELECT a FROM Areasseleccion a WHERE a.orden = :orden"),
    @NamedQuery(name = "Areasseleccion.findByPrueba", query = "SELECT a FROM Areasseleccion a WHERE a.prueba = :prueba"),
    @NamedQuery(name = "Areasseleccion.findByDescripcion", query = "SELECT a FROM Areasseleccion a WHERE a.descripcion = :descripcion")})
public class Areasseleccion implements Serializable {
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
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "ponderacion")
    private BigDecimal ponderacion;
    @Column(name = "notamaxima")
    private BigDecimal notamaxima;
    @Column(name = "orden")
    private Integer orden;
    @Column(name = "prueba")
    private Boolean prueba;
    @Size(max = 2147483647)
    @Column(name = "descripcion")
    private String descripcion;
    @OneToMany(mappedBy = "area")
    private List<Detallesoposicion> detallesoposicionList;
    @OneToMany(mappedBy = "area")
    private List<Calificacionesempleado> calificacionesempleadoList;
    @OneToMany(mappedBy = "area")
    private List<Valoresrequerimientos> valoresrequerimientosList;
    @JoinColumn(name = "tipo", referencedColumnName = "id")
    @ManyToOne
    private Tiposseleccion tipo;

    public Areasseleccion() {
    }

    public Areasseleccion(Integer id) {
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

    public Boolean getPrueba() {
        return prueba;
    }

    public void setPrueba(Boolean prueba) {
        this.prueba = prueba;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @XmlTransient
    public List<Detallesoposicion> getDetallesoposicionList() {
        return detallesoposicionList;
    }

    public void setDetallesoposicionList(List<Detallesoposicion> detallesoposicionList) {
        this.detallesoposicionList = detallesoposicionList;
    }

    @XmlTransient
    public List<Calificacionesempleado> getCalificacionesempleadoList() {
        return calificacionesempleadoList;
    }

    public void setCalificacionesempleadoList(List<Calificacionesempleado> calificacionesempleadoList) {
        this.calificacionesempleadoList = calificacionesempleadoList;
    }

    @XmlTransient
    public List<Valoresrequerimientos> getValoresrequerimientosList() {
        return valoresrequerimientosList;
    }

    public void setValoresrequerimientosList(List<Valoresrequerimientos> valoresrequerimientosList) {
        this.valoresrequerimientosList = valoresrequerimientosList;
    }

    public Tiposseleccion getTipo() {
        return tipo;
    }

    public void setTipo(Tiposseleccion tipo) {
        this.tipo = tipo;
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
        if (!(object instanceof Areasseleccion)) {
            return false;
        }
        Areasseleccion other = (Areasseleccion) object;
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
