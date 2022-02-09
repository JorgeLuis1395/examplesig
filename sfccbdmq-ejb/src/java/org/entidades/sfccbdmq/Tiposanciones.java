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
@Table(name = "tiposanciones")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tiposanciones.findAll", query = "SELECT t FROM Tiposanciones t"),
    @NamedQuery(name = "Tiposanciones.findById", query = "SELECT t FROM Tiposanciones t WHERE t.id = :id"),
    @NamedQuery(name = "Tiposanciones.findByNombre", query = "SELECT t FROM Tiposanciones t WHERE t.nombre = :nombre"),
    @NamedQuery(name = "Tiposanciones.findBySancion", query = "SELECT t FROM Tiposanciones t WHERE t.sancion = :sancion")})
public class Tiposanciones implements Serializable {
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
    @Column(name = "sancion")
    private BigDecimal sancion;
    @Column(name = "especunaria")
    private Boolean especunaria;
    @Column(name = "esleve")
    private Boolean esleve;
    @JoinColumn(name = "tipo", referencedColumnName = "id")
    @ManyToOne
    private Codigos tipo;
    @OneToMany(mappedBy = "tipo")
    private List<Historialsanciones> historialsancionesList;

    public Tiposanciones() {
    }

    public Tiposanciones(Integer id) {
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

    public BigDecimal getSancion() {
        return sancion;
    }

    public void setSancion(BigDecimal sancion) {
        this.sancion = sancion;
    }

    public Codigos getTipo() {
        return tipo;
    }

    public void setTipo(Codigos tipo) {
        this.tipo = tipo;
    }

    @XmlTransient
    public List<Historialsanciones> getHistorialsancionesList() {
        return historialsancionesList;
    }

    public void setHistorialsancionesList(List<Historialsanciones> historialsancionesList) {
        this.historialsancionesList = historialsancionesList;
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
        if (!(object instanceof Tiposanciones)) {
            return false;
        }
        Tiposanciones other = (Tiposanciones) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nombre;
    }

    /**
     * @return the especunaria
     */
    public Boolean getEspecunaria() {
        return especunaria;
    }

    /**
     * @param especunaria the especunaria to set
     */
    public void setEspecunaria(Boolean especunaria) {
        this.especunaria = especunaria;
    }

    /**
     * @return the esleve
     */
    public Boolean getEsleve() {
        return esleve;
    }

    /**
     * @param esleve the esleve to set
     */
    public void setEsleve(Boolean esleve) {
        this.esleve = esleve;
    }
    
}
