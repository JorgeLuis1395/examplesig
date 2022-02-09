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
@Table(name = "unidades")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Unidades.findAll", query = "SELECT u FROM Unidades u"),
    @NamedQuery(name = "Unidades.findById", query = "SELECT u FROM Unidades u WHERE u.id = :id"),
    @NamedQuery(name = "Unidades.findByEquivalencia", query = "SELECT u FROM Unidades u WHERE u.equivalencia = :equivalencia"),
    @NamedQuery(name = "Unidades.findByFactor", query = "SELECT u FROM Unidades u WHERE u.factor = :factor")})
public class Unidades implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "equivalencia")
    private String equivalencia;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "factor")
    private Float factor;
    @OneToMany(mappedBy = "unidad")
    private List<Bodegasuministro> bodegasuministroList;
    @OneToMany(mappedBy = "base")
    private List<Unidades> unidadesList;
    @JoinColumn(name = "base", referencedColumnName = "id")
    @ManyToOne
    private Unidades base;
    @OneToMany(mappedBy = "unidad")
    private List<Suministros> suministrosList;
    @OneToMany(mappedBy = "unidad")
    private List<Kardexinventario> kardexinventarioList;

    public Unidades() {
    }

    public Unidades(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEquivalencia() {
        return equivalencia;
    }

    public void setEquivalencia(String equivalencia) {
        this.equivalencia = equivalencia;
    }

    public Float getFactor() {
        return factor;
    }

    public void setFactor(Float factor) {
        this.factor = factor;
    }

    @XmlTransient
    public List<Bodegasuministro> getBodegasuministroList() {
        return bodegasuministroList;
    }

    public void setBodegasuministroList(List<Bodegasuministro> bodegasuministroList) {
        this.bodegasuministroList = bodegasuministroList;
    }

    @XmlTransient
    public List<Unidades> getUnidadesList() {
        return unidadesList;
    }

    public void setUnidadesList(List<Unidades> unidadesList) {
        this.unidadesList = unidadesList;
    }

    public Unidades getBase() {
        return base;
    }

    public void setBase(Unidades base) {
        this.base = base;
    }

    @XmlTransient
    public List<Suministros> getSuministrosList() {
        return suministrosList;
    }

    public void setSuministrosList(List<Suministros> suministrosList) {
        this.suministrosList = suministrosList;
    }

    @XmlTransient
    public List<Kardexinventario> getKardexinventarioList() {
        return kardexinventarioList;
    }

    public void setKardexinventarioList(List<Kardexinventario> kardexinventarioList) {
        this.kardexinventarioList = kardexinventarioList;
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
        if (!(object instanceof Unidades)) {
            return false;
        }
        Unidades other = (Unidades) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return equivalencia;
    }
    
}
