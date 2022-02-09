/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "adicionalkardex")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Adicionalkardex.findAll", query = "SELECT a FROM Adicionalkardex a"),
    @NamedQuery(name = "Adicionalkardex.findById", query = "SELECT a FROM Adicionalkardex a WHERE a.id = :id"),
    @NamedQuery(name = "Adicionalkardex.findByValornumerico", query = "SELECT a FROM Adicionalkardex a WHERE a.valornumerico = :valornumerico"),
    @NamedQuery(name = "Adicionalkardex.findByValortexto", query = "SELECT a FROM Adicionalkardex a WHERE a.valortexto = :valortexto"),
    @NamedQuery(name = "Adicionalkardex.findByValorfecha", query = "SELECT a FROM Adicionalkardex a WHERE a.valorfecha = :valorfecha")})
public class Adicionalkardex implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valornumerico")
    private Float valornumerico;
    @Size(max = 2147483647)
    @Column(name = "valortexto")
    private String valortexto;
    @Column(name = "valorfecha")
    @Temporal(TemporalType.DATE)
    private Date valorfecha;
    @JoinColumn(name = "kardex", referencedColumnName = "id")
    @ManyToOne
    private Kardexinventario kardex;
    @JoinColumn(name = "adicional", referencedColumnName = "id")
    @ManyToOne
    private Adicionalessuministro adicional;

    public Adicionalkardex() {
    }

    public Adicionalkardex(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Float getValornumerico() {
        return valornumerico;
    }

    public void setValornumerico(Float valornumerico) {
        this.valornumerico = valornumerico;
    }

    public String getValortexto() {
        return valortexto;
    }

    public void setValortexto(String valortexto) {
        this.valortexto = valortexto;
    }

    public Date getValorfecha() {
        return valorfecha;
    }

    public void setValorfecha(Date valorfecha) {
        this.valorfecha = valorfecha;
    }

    public Kardexinventario getKardex() {
        return kardex;
    }

    public void setKardex(Kardexinventario kardex) {
        this.kardex = kardex;
    }

    public Adicionalessuministro getAdicional() {
        return adicional;
    }

    public void setAdicional(Adicionalessuministro adicional) {
        this.adicional = adicional;
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
        if (!(object instanceof Adicionalkardex)) {
            return false;
        }
        Adicionalkardex other = (Adicionalkardex) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Adicionalkardex[ id=" + id + " ]";
    }
    
}
