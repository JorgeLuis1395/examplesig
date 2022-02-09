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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "pondnivelgestion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Pondnivelgestion.findAll", query = "SELECT p FROM Pondnivelgestion p"),
    @NamedQuery(name = "Pondnivelgestion.findById", query = "SELECT p FROM Pondnivelgestion p WHERE p.id = :id"),
    @NamedQuery(name = "Pondnivelgestion.findByActivo", query = "SELECT p FROM Pondnivelgestion p WHERE p.activo = :activo"),
    @NamedQuery(name = "Pondnivelgestion.findByPonderacion", query = "SELECT p FROM Pondnivelgestion p WHERE p.ponderacion = :ponderacion")})
public class Pondnivelgestion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "activo")
    private Boolean activo;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "ponderacion")
    private BigDecimal ponderacion;
    @JoinColumn(name = "nivelgestion", referencedColumnName = "id")
    @ManyToOne
    private Nivelesgestion nivelgestion;
    @JoinColumn(name = "aspecto", referencedColumnName = "id")
    @ManyToOne
    private Aspectos aspecto;

    public Pondnivelgestion() {
    }

    public Pondnivelgestion(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Nivelesgestion getNivelgestion() {
        return nivelgestion;
    }

    public void setNivelgestion(Nivelesgestion nivelgestion) {
        this.nivelgestion = nivelgestion;
    }

    public Aspectos getAspecto() {
        return aspecto;
    }

    public void setAspecto(Aspectos aspecto) {
        this.aspecto = aspecto;
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
        if (!(object instanceof Pondnivelgestion)) {
            return false;
        }
        Pondnivelgestion other = (Pondnivelgestion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Pondnivelgestion[ id=" + id + " ]";
    }
    
}
