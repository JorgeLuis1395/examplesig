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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "entrevistas")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Entrevistas.findAll", query = "SELECT e FROM Entrevistas e"),
    @NamedQuery(name = "Entrevistas.findById", query = "SELECT e FROM Entrevistas e WHERE e.id = :id"),
    @NamedQuery(name = "Entrevistas.findByFecha", query = "SELECT e FROM Entrevistas e WHERE e.fecha = :fecha"),
    @NamedQuery(name = "Entrevistas.findByObservacion", query = "SELECT e FROM Entrevistas e WHERE e.observacion = :observacion")})
public class Entrevistas implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Size(max = 2147483647)
    @Column(name = "observacion")
    private String observacion;
    @OneToMany(mappedBy = "entrevista")
    private List<Calificacionesoposicion> calificacionesoposicionList;
    @JoinColumn(name = "postulacion", referencedColumnName = "id")
    @ManyToOne
    private Postulaciones postulacion;
    @JoinColumn(name = "responsable", referencedColumnName = "id")
    @ManyToOne
    private Entidades responsable;
    @OneToMany(mappedBy = "entrevista")
    private List<Detallesentrevista> detallesentrevistaList;

    public Entrevistas() {
    }

    public Entrevistas(Integer id) {
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

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    @XmlTransient
    public List<Calificacionesoposicion> getCalificacionesoposicionList() {
        return calificacionesoposicionList;
    }

    public void setCalificacionesoposicionList(List<Calificacionesoposicion> calificacionesoposicionList) {
        this.calificacionesoposicionList = calificacionesoposicionList;
    }

    public Postulaciones getPostulacion() {
        return postulacion;
    }

    public void setPostulacion(Postulaciones postulacion) {
        this.postulacion = postulacion;
    }

    public Entidades getResponsable() {
        return responsable;
    }

    public void setResponsable(Entidades responsable) {
        this.responsable = responsable;
    }

    @XmlTransient
    public List<Detallesentrevista> getDetallesentrevistaList() {
        return detallesentrevistaList;
    }

    public void setDetallesentrevistaList(List<Detallesentrevista> detallesentrevistaList) {
        this.detallesentrevistaList = detallesentrevistaList;
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
        if (!(object instanceof Entrevistas)) {
            return false;
        }
        Entrevistas other = (Entrevistas) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Entrevistas[ id=" + id + " ]";
    }
    
}
