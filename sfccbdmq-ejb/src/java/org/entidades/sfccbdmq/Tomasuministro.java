/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
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
@Table(name = "tomasuministro")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tomasuministro.findAll", query = "SELECT t FROM Tomasuministro t"),
    @NamedQuery(name = "Tomasuministro.findById", query = "SELECT t FROM Tomasuministro t WHERE t.id = :id"),
    @NamedQuery(name = "Tomasuministro.findBySaldo", query = "SELECT t FROM Tomasuministro t WHERE t.saldo = :saldo"),
    @NamedQuery(name = "Tomasuministro.findByConstatado", query = "SELECT t FROM Tomasuministro t WHERE t.constatado = :constatado")})
public class Tomasuministro implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "saldo")
    private Float saldo;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "constatado")
    private Float constatado;
    @JoinColumn(name = "tomafisica", referencedColumnName = "id")
    @ManyToOne
    private Tomafisicasuministros tomafisica;
    @JoinColumn(name = "suministro", referencedColumnName = "id")
    @ManyToOne
    private Suministros suministro;

    public Tomasuministro() {
    }

    public Tomasuministro(Integer id) {
        this.id = id;
    }

    public Tomasuministro(Integer id, float saldo) {
        this.id = id;
        this.saldo = saldo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Float getSaldo() {
        return saldo;
    }

    public void setSaldo(Float saldo) {
        this.saldo = saldo;
    }

    public Float getConstatado() {
        return constatado;
    }

    public void setConstatado(Float constatado) {
        this.constatado = constatado;
    }

    public Tomafisicasuministros getTomafisica() {
        return tomafisica;
    }

    public void setTomafisica(Tomafisicasuministros tomafisica) {
        this.tomafisica = tomafisica;
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
        if (!(object instanceof Tomasuministro)) {
            return false;
        }
        Tomasuministro other = (Tomasuministro) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Tomasuministro[ id=" + id + " ]";
    }
    
}
