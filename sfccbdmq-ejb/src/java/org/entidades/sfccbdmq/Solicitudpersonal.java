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
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author luis
 */
@Entity
@Table(name = "solicitudpersonal")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Solicitudpersonal.findAll", query = "SELECT s FROM Solicitudpersonal s")
    , @NamedQuery(name = "Solicitudpersonal.findById", query = "SELECT s FROM Solicitudpersonal s WHERE s.id = :id")
    , @NamedQuery(name = "Solicitudpersonal.findByDescripcion", query = "SELECT s FROM Solicitudpersonal s WHERE s.descripcion = :descripcion")
    , @NamedQuery(name = "Solicitudpersonal.findByActivo", query = "SELECT s FROM Solicitudpersonal s WHERE s.activo = :activo")
    , @NamedQuery(name = "Solicitudpersonal.findByFechasolicitud", query = "SELECT s FROM Solicitudpersonal s WHERE s.fechasolicitud = :fechasolicitud")})
public class Solicitudpersonal implements Serializable {

    @Size(max = 2147483647)
    @Column(name = "numero")
    private String numero;

    @Size(max = 2147483647)
    @Column(name = "estado")
    private String estado;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "activo")
    private Boolean activo;
    @Column(name = "fechasolicitud")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechasolicitud;
    @JoinColumn(name = "empleado", referencedColumnName = "id")
    @ManyToOne
    private Empleados empleado;
    @OneToMany(mappedBy = "solicitudpersonal")
    private List<Detallesolicitudpersonal> detallesolicitudpersonalList;

    public Solicitudpersonal() {
    }

    public Solicitudpersonal(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Date getFechasolicitud() {
        return fechasolicitud;
    }

    public void setFechasolicitud(Date fechasolicitud) {
        this.fechasolicitud = fechasolicitud;
    }

    public Empleados getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleados empleado) {
        this.empleado = empleado;
    }

    @XmlTransient
    @JsonIgnore
    public List<Detallesolicitudpersonal> getDetallesolicitudpersonalList() {
        return detallesolicitudpersonalList;
    }

    public void setDetallesolicitudpersonalList(List<Detallesolicitudpersonal> detallesolicitudpersonalList) {
        this.detallesolicitudpersonalList = detallesolicitudpersonalList;
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
        if (!(object instanceof Solicitudpersonal)) {
            return false;
        }
        Solicitudpersonal other = (Solicitudpersonal) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.sfccbdmq.Solicitudpersonal[ id=" + id + " ]";
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }
    
}