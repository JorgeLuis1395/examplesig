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
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "valoracionesdesempenio")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Valoracionesdesempenio.findAll", query = "SELECT v FROM Valoracionesdesempenio v"),
    @NamedQuery(name = "Valoracionesdesempenio.findById", query = "SELECT v FROM Valoracionesdesempenio v WHERE v.id = :id"),
    @NamedQuery(name = "Valoracionesdesempenio.findByNombre", query = "SELECT v FROM Valoracionesdesempenio v WHERE v.nombre = :nombre"),
    @NamedQuery(name = "Valoracionesdesempenio.findByMinimo", query = "SELECT v FROM Valoracionesdesempenio v WHERE v.minimo = :minimo"),
    @NamedQuery(name = "Valoracionesdesempenio.findByMaximo", query = "SELECT v FROM Valoracionesdesempenio v WHERE v.maximo = :maximo"),
    @NamedQuery(name = "Valoracionesdesempenio.findByDescripcion", query = "SELECT v FROM Valoracionesdesempenio v WHERE v.descripcion = :descripcion"),
    @NamedQuery(name = "Valoracionesdesempenio.findByActivo", query = "SELECT v FROM Valoracionesdesempenio v WHERE v.activo = :activo")})
public class Valoracionesdesempenio implements Serializable {
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
    @Column(name = "minimo")
    private BigDecimal minimo;
    @Column(name = "maximo")
    private BigDecimal maximo;
    @Size(max = 2147483647)
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "activo")
    private Boolean activo;
    @OneToMany(mappedBy = "valoracion")
    private List<Verificacionesdesempenio> verificacionesdesempenioList;
    @Transient
    private BigDecimal nota;
    public Valoracionesdesempenio() {
    }

    public Valoracionesdesempenio(Integer id) {
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

    public BigDecimal getMinimo() {
        return minimo;
    }

    public void setMinimo(BigDecimal minimo) {
        this.minimo = minimo;
    }

    public BigDecimal getMaximo() {
        return maximo;
    }

    public void setMaximo(BigDecimal maximo) {
        this.maximo = maximo;
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

    @XmlTransient
    public List<Verificacionesdesempenio> getVerificacionesdesempenioList() {
        return verificacionesdesempenioList;
    }

    public void setVerificacionesdesempenioList(List<Verificacionesdesempenio> verificacionesdesempenioList) {
        this.verificacionesdesempenioList = verificacionesdesempenioList;
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
        if (!(object instanceof Valoracionesdesempenio)) {
            return false;
        }
        Valoracionesdesempenio other = (Valoracionesdesempenio) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Valoracionesdesempenio[ id=" + id + " ]";
    }

    /**
     * @return the nota
     */
    public BigDecimal getNota() {
        return nota;
    }

    /**
     * @param nota the nota to set
     */
    public void setNota(BigDecimal nota) {
        this.nota = nota;
    }
    
}
