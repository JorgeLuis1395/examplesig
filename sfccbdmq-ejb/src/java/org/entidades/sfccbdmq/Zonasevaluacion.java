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
@Table(name = "zonasevaluacion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Zonasevaluacion.findAll", query = "SELECT z FROM Zonasevaluacion z"),
    @NamedQuery(name = "Zonasevaluacion.findById", query = "SELECT z FROM Zonasevaluacion z WHERE z.id = :id"),
    @NamedQuery(name = "Zonasevaluacion.findByZonaevaluacion", query = "SELECT z FROM Zonasevaluacion z WHERE z.zonaevaluacion = :zonaevaluacion"),
    @NamedQuery(name = "Zonasevaluacion.findByPonderacion", query = "SELECT z FROM Zonasevaluacion z WHERE z.ponderacion = :ponderacion"),
    @NamedQuery(name = "Zonasevaluacion.findByActivo", query = "SELECT z FROM Zonasevaluacion z WHERE z.activo = :activo"),
    @NamedQuery(name = "Zonasevaluacion.findByTotal", query = "SELECT z FROM Zonasevaluacion z WHERE z.total = :total")})
public class Zonasevaluacion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "zonaevaluacion")
    private String zonaevaluacion;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "ponderacion")
    private BigDecimal ponderacion;
    @Column(name = "activo")
    private Boolean activo;
    @Column(name = "total")
    private BigDecimal total;
    @OneToMany(mappedBy = "zonaevaluacion")
    private List<Aspectos> aspectosList;

    public Zonasevaluacion() {
    }

    public Zonasevaluacion(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getZonaevaluacion() {
        return zonaevaluacion;
    }

    public void setZonaevaluacion(String zonaevaluacion) {
        this.zonaevaluacion = zonaevaluacion;
    }

    public BigDecimal getPonderacion() {
        return ponderacion;
    }

    public void setPonderacion(BigDecimal ponderacion) {
        this.ponderacion = ponderacion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    @XmlTransient
    public List<Aspectos> getAspectosList() {
        return aspectosList;
    }

    public void setAspectosList(List<Aspectos> aspectosList) {
        this.aspectosList = aspectosList;
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
        if (!(object instanceof Zonasevaluacion)) {
            return false;
        }
        Zonasevaluacion other = (Zonasevaluacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Zonasevaluacion[ id=" + id + " ]";
    }
    
}
