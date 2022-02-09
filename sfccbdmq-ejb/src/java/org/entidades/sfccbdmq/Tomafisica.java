/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "tomafisica")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tomafisica.findAll", query = "SELECT t FROM Tomafisica t"),
    @NamedQuery(name = "Tomafisica.findById", query = "SELECT t FROM Tomafisica t WHERE t.id = :id"),
    @NamedQuery(name = "Tomafisica.findByFecha", query = "SELECT t FROM Tomafisica t WHERE t.fecha = :fecha"),
    @NamedQuery(name = "Tomafisica.findByFechafin", query = "SELECT t FROM Tomafisica t WHERE t.fechafin = :fechafin")})
public class Tomafisica implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Column(name = "fechafin")
    @Temporal(TemporalType.DATE)
    private Date fechafin;
    @OneToMany(mappedBy = "toma")
    private List<Detalletoma> detalletomaList;
    @JoinColumn(name = "responsable", referencedColumnName = "id")
    @ManyToOne
    private Empleados responsable;

    public Tomafisica() {
    }

    public Tomafisica(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Date getFechafin() {
        return fechafin;
    }

    public void setFechafin(Date fechafin) {
        this.fechafin = fechafin;
    }

    @XmlTransient
    public List<Detalletoma> getDetalletomaList() {
        return detalletomaList;
    }

    public void setDetalletomaList(List<Detalletoma> detalletomaList) {
        this.detalletomaList = detalletomaList;
    }

    public Empleados getResponsable() {
        return responsable;
    }

    public void setResponsable(Empleados responsable) {
        this.responsable = responsable;
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
        if (!(object instanceof Tomafisica)) {
            return false;
        }
        Tomafisica other = (Tomafisica) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Tomafisica[ id=" + id + " ]";
    }
    
}
