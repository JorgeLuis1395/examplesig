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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "activoobligacion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Activoobligacion.findAll", query = "SELECT a FROM Activoobligacion a"),
    @NamedQuery(name = "Activoobligacion.findById", query = "SELECT a FROM Activoobligacion a WHERE a.id = :id"),
    @NamedQuery(name = "Activoobligacion.findByFecha", query = "SELECT a FROM Activoobligacion a WHERE a.fecha = :fecha"),
    @NamedQuery(name = "Activoobligacion.findByValor", query = "SELECT a FROM Activoobligacion a WHERE a.valor = :valor")})
public class Activoobligacion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private Float valor;
    @JoinColumn(name = "obligacion", referencedColumnName = "id")
    @ManyToOne
    private Obligaciones obligacion;
    @JoinColumn(name = "activo", referencedColumnName = "id")
    @ManyToOne
    private Activos activo;

    public Activoobligacion() {
    }

    public Activoobligacion(Integer id) {
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

    public Float getValor() {
        return valor;
    }

    public void setValor(Float valor) {
        this.valor = valor;
    }

    public Obligaciones getObligacion() {
        return obligacion;
    }

    public void setObligacion(Obligaciones obligacion) {
        this.obligacion = obligacion;
    }

    public Activos getActivo() {
        return activo;
    }

    public void setActivo(Activos activo) {
        this.activo = activo;
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
        if (!(object instanceof Activoobligacion)) {
            return false;
        }
        Activoobligacion other = (Activoobligacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Activoobligacion[ id=" + id + " ]";
    }
    
}
