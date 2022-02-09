/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
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
@Table(name = "aspectos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Aspectos.findAll", query = "SELECT a FROM Aspectos a"),
    @NamedQuery(name = "Aspectos.findById", query = "SELECT a FROM Aspectos a WHERE a.id = :id"),
    @NamedQuery(name = "Aspectos.findByAspecto", query = "SELECT a FROM Aspectos a WHERE a.aspecto = :aspecto"),
    @NamedQuery(name = "Aspectos.findByDescripcion", query = "SELECT a FROM Aspectos a WHERE a.descripcion = :descripcion"),
    @NamedQuery(name = "Aspectos.findByActivo", query = "SELECT a FROM Aspectos a WHERE a.activo = :activo")})
public class Aspectos implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "aspecto")
    private String aspecto;
    @Size(max = 2147483647)
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "activo")
    private Boolean activo;
    @JoinColumn(name = "zonaevaluacion", referencedColumnName = "id")
    @ManyToOne
    private Zonasevaluacion zonaevaluacion;
    @OneToMany(mappedBy = "aspecto")
    private List<Pondnivelgestion> pondnivelgestionList;

    public Aspectos() {
    }

    public Aspectos(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAspecto() {
        return aspecto;
    }

    public void setAspecto(String aspecto) {
        this.aspecto = aspecto;
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

    public Zonasevaluacion getZonaevaluacion() {
        return zonaevaluacion;
    }

    public void setZonaevaluacion(Zonasevaluacion zonaevaluacion) {
        this.zonaevaluacion = zonaevaluacion;
    }

    @XmlTransient
    public List<Pondnivelgestion> getPondnivelgestionList() {
        return pondnivelgestionList;
    }

    public void setPondnivelgestionList(List<Pondnivelgestion> pondnivelgestionList) {
        this.pondnivelgestionList = pondnivelgestionList;
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
        if (!(object instanceof Aspectos)) {
            return false;
        }
        Aspectos other = (Aspectos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Aspectos[ id=" + id + " ]";
    }
    
}
