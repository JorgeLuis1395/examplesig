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
@Table(name = "adicionalessuministro")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Adicionalessuministro.findAll", query = "SELECT a FROM Adicionalessuministro a"),
    @NamedQuery(name = "Adicionalessuministro.findById", query = "SELECT a FROM Adicionalessuministro a WHERE a.id = :id"),
    @NamedQuery(name = "Adicionalessuministro.findByNombre", query = "SELECT a FROM Adicionalessuministro a WHERE a.nombre = :nombre"),
    @NamedQuery(name = "Adicionalessuministro.findByTipodato", query = "SELECT a FROM Adicionalessuministro a WHERE a.tipodato = :tipodato"),
    @NamedQuery(name = "Adicionalessuministro.findByObligatorio", query = "SELECT a FROM Adicionalessuministro a WHERE a.obligatorio = :obligatorio"),
    @NamedQuery(name = "Adicionalessuministro.findByIndividual", query = "SELECT a FROM Adicionalessuministro a WHERE a.individual = :individual")})
public class Adicionalessuministro implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "tipodato")
    private Integer tipodato;
    @Column(name = "obligatorio")
    private Boolean obligatorio;
    @Column(name = "individual")
    private Boolean individual;
    @OneToMany(mappedBy = "adicional")
    private List<Adicionalkardex> adicionalkardexList;
    @JoinColumn(name = "suministro", referencedColumnName = "id")
    @ManyToOne
    private Suministros suministro;

    public Adicionalessuministro() {
    }

    public Adicionalessuministro(Integer id) {
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

    public Integer getTipodato() {
        return tipodato;
    }

    public void setTipodato(Integer tipodato) {
        this.tipodato = tipodato;
    }

    public Boolean getObligatorio() {
        return obligatorio;
    }

    public void setObligatorio(Boolean obligatorio) {
        this.obligatorio = obligatorio;
    }

    public Boolean getIndividual() {
        return individual;
    }

    public void setIndividual(Boolean individual) {
        this.individual = individual;
    }

    @XmlTransient
    public List<Adicionalkardex> getAdicionalkardexList() {
        return adicionalkardexList;
    }

    public void setAdicionalkardexList(List<Adicionalkardex> adicionalkardexList) {
        this.adicionalkardexList = adicionalkardexList;
    }

    public Suministros getSuministro() {
        return suministro;
    }

    public void setSuministro(Suministros suministro) {
        this.suministro = suministro;
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
        if (!(object instanceof Adicionalessuministro)) {
            return false;
        }
        Adicionalessuministro other = (Adicionalessuministro) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Adicionalessuministro[ id=" + id + " ]";
    }
    
}
