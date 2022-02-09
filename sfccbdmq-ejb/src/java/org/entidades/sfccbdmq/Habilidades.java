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
@Table(name = "habilidades")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Habilidades.findAll", query = "SELECT h FROM Habilidades h"),
    @NamedQuery(name = "Habilidades.findById", query = "SELECT h FROM Habilidades h WHERE h.id = :id"),
    @NamedQuery(name = "Habilidades.findByHabilidad", query = "SELECT h FROM Habilidades h WHERE h.habilidad = :habilidad"),
    @NamedQuery(name = "Habilidades.findByInteres", query = "SELECT h FROM Habilidades h WHERE h.interes = :interes")})
public class Habilidades implements Serializable {
    @Column(name = "fechaingreso")
    @Temporal(TemporalType.DATE)
    private Date fechaingreso;
    @Size(max = 2147483647)
    @Column(name = "responvalid")
    private String responvalid;
    @Size(max = 2147483647)
    @Column(name = "obsvalidado")
    private String obsvalidado;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "habilidad")
    private String habilidad;
    @Column(name = "interes")
    private Boolean interes;
    @JoinColumn(name = "empleado", referencedColumnName = "id")
    @ManyToOne
    private Empleados empleado;

    public Habilidades() {
    }

    public Habilidades(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHabilidad() {
        return habilidad;
    }

    public void setHabilidad(String habilidad) {
        this.habilidad = habilidad;
    }

    public Boolean getInteres() {
        return interes;
    }

    public void setInteres(Boolean interes) {
        this.interes = interes;
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
        if (!(object instanceof Habilidades)) {
            return false;
        }
        Habilidades other = (Habilidades) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Habilidades[ id=" + id + " ]";
    }

    public String getResponvalid() {
        return responvalid;
    }

    public void setResponvalid(String responvalid) {
        this.responvalid = responvalid;
    }

    public String getObsvalidado() {
        return obsvalidado;
    }

    public void setObsvalidado(String obsvalidado) {
        this.obsvalidado = obsvalidado;
    }

    public Date getFechaingreso() {
        return fechaingreso;
    }

    public void setFechaingreso(Date fechaingreso) {
        this.fechaingreso = fechaingreso;
    }
    
}
