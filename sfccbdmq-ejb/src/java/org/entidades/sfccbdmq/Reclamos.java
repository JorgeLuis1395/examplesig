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
@Table(name = "reclamos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Reclamos.findAll", query = "SELECT r FROM Reclamos r"),
    @NamedQuery(name = "Reclamos.findById", query = "SELECT r FROM Reclamos r WHERE r.id = :id"),
    @NamedQuery(name = "Reclamos.findByFecha", query = "SELECT r FROM Reclamos r WHERE r.fecha = :fecha"),
    @NamedQuery(name = "Reclamos.findByNumeroreclamo", query = "SELECT r FROM Reclamos r WHERE r.numeroreclamo = :numeroreclamo"),
    @NamedQuery(name = "Reclamos.findByValor", query = "SELECT r FROM Reclamos r WHERE r.valor = :valor")})
public class Reclamos implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Size(max = 2147483647)
    @Column(name = "numeroreclamo")
    private String numeroreclamo;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private Float valor;
    @JoinColumn(name = "poliza", referencedColumnName = "id")
    @ManyToOne
    private Polizas poliza;
    @JoinColumn(name = "activo", referencedColumnName = "id")
    @ManyToOne
    private Activos activo;

    public Reclamos() {
    }

    public Reclamos(Integer id) {
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

    public String getNumeroreclamo() {
        return numeroreclamo;
    }

    public void setNumeroreclamo(String numeroreclamo) {
        this.numeroreclamo = numeroreclamo;
    }

    public Float getValor() {
        return valor;
    }

    public void setValor(Float valor) {
        this.valor = valor;
    }

    public Polizas getPoliza() {
        return poliza;
    }

    public void setPoliza(Polizas poliza) {
        this.poliza = poliza;
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
        if (!(object instanceof Reclamos)) {
            return false;
        }
        Reclamos other = (Reclamos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Reclamos[ id=" + id + " ]";
    }
    
}
