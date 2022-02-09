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

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "polizas")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Polizas.findAll", query = "SELECT p FROM Polizas p"),
    @NamedQuery(name = "Polizas.findById", query = "SELECT p FROM Polizas p WHERE p.id = :id"),
    @NamedQuery(name = "Polizas.findByPrima", query = "SELECT p FROM Polizas p WHERE p.prima = :prima"),
    @NamedQuery(name = "Polizas.findByDescripcion", query = "SELECT p FROM Polizas p WHERE p.descripcion = :descripcion"),
    @NamedQuery(name = "Polizas.findByDesde", query = "SELECT p FROM Polizas p WHERE p.desde = :desde"),
    @NamedQuery(name = "Polizas.findByHasta", query = "SELECT p FROM Polizas p WHERE p.hasta = :hasta")})
public class Polizas implements Serializable {
    @OneToMany(mappedBy = "poliza")
    private List<Activos> activosList;
    @OneToMany(mappedBy = "poliza")
    private List<Reclamos> reclamosList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "prima")
    private BigDecimal prima;
    @Size(max = 2147483647)
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "desde")
    @Temporal(TemporalType.DATE)
    private Date desde;
    @Column(name = "hasta")
    @Temporal(TemporalType.DATE)
    private Date hasta;
    @OneToMany(mappedBy = "poliza")
    private List<Activopoliza> activopolizaList;
    @JoinColumn(name = "aseguradora", referencedColumnName = "id")
    @ManyToOne
    private Codigos aseguradora;

    public Polizas() {
    }

    public Polizas(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getPrima() {
        return prima;
    }

    public void setPrima(BigDecimal prima) {
        this.prima = prima;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getDesde() {
        return desde;
    }

    public void setDesde(Date desde) {
        this.desde = desde;
    }

    public Date getHasta() {
        return hasta;
    }

    public void setHasta(Date hasta) {
        this.hasta = hasta;
    }

    @XmlTransient
    public List<Activopoliza> getActivopolizaList() {
        return activopolizaList;
    }

    public void setActivopolizaList(List<Activopoliza> activopolizaList) {
        this.activopolizaList = activopolizaList;
    }

    public Codigos getAseguradora() {
        return aseguradora;
    }

    public void setAseguradora(Codigos aseguradora) {
        this.aseguradora = aseguradora;
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
        if (!(object instanceof Polizas)) {
            return false;
        }
        Polizas other = (Polizas) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return descripcion;
    }

    @XmlTransient
    public List<Activos> getActivosList() {
        return activosList;
    }

    public void setActivosList(List<Activos> activosList) {
        this.activosList = activosList;
    }

    @XmlTransient
    public List<Reclamos> getReclamosList() {
        return reclamosList;
    }

    public void setReclamosList(List<Reclamos> reclamosList) {
        this.reclamosList = reclamosList;
    }
    
}
