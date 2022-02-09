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
@Table(name = "comisionpostulacion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Comisionpostulacion.findAll", query = "SELECT c FROM Comisionpostulacion c"),
    @NamedQuery(name = "Comisionpostulacion.findById", query = "SELECT c FROM Comisionpostulacion c WHERE c.id = :id"),
    @NamedQuery(name = "Comisionpostulacion.findByActivo", query = "SELECT c FROM Comisionpostulacion c WHERE c.activo = :activo")})
public class Comisionpostulacion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "activo")
    private Boolean activo;
    @JoinColumn(name = "solicitud", referencedColumnName = "id")
    @ManyToOne
    private Solicitudescargo solicitud;
    @JoinColumn(name = "empleado", referencedColumnName = "id")
    @ManyToOne
    private Empleados empleado;

    public Comisionpostulacion() {
    }

    public Comisionpostulacion(Integer id) {
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

    public Solicitudescargo getSolicitud() {
        return solicitud;
    }

    public void setSolicitud(Solicitudescargo solicitud) {
        this.solicitud = solicitud;
    }

    public Empleados getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleados empleado) {
        this.empleado = empleado;
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
        if (!(object instanceof Comisionpostulacion)) {
            return false;
        }
        Comisionpostulacion other = (Comisionpostulacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Comisionpostulacion[ id=" + id + " ]";
    }
    
}
