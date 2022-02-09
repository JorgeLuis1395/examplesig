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
 * @author luis
 */
@Entity
@Table(name = "rasconciliaciones")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Rasconciliaciones.findAll", query = "SELECT r FROM Rasconciliaciones r"),
    @NamedQuery(name = "Rasconciliaciones.findById", query = "SELECT r FROM Rasconciliaciones r WHERE r.id = :id"),
    @NamedQuery(name = "Rasconciliaciones.findByManual", query = "SELECT r FROM Rasconciliaciones r WHERE r.manual = :manual")})
public class Rasconciliaciones implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "manual")
    private Boolean manual;
    @JoinColumn(name = "renglon", referencedColumnName = "id")
    @ManyToOne
    private Renglones renglon;
    @JoinColumn(name = "detalle", referencedColumnName = "id")
    @ManyToOne
    private Detalleconciliaciones detalle;

    public Rasconciliaciones() {
    }

    public Rasconciliaciones(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getManual() {
        return manual;
    }

    public void setManual(Boolean manual) {
        this.manual = manual;
    }

    public Renglones getRenglon() {
        return renglon;
    }

    public void setRenglon(Renglones renglon) {
        this.renglon = renglon;
    }

    public Detalleconciliaciones getDetalle() {
        return detalle;
    }

    public void setDetalle(Detalleconciliaciones detalle) {
        this.detalle = detalle;
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
        if (!(object instanceof Rasconciliaciones)) {
            return false;
        }
        Rasconciliaciones other = (Rasconciliaciones) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Rasconciliaciones[ id=" + id + " ]";
    }
    
}
