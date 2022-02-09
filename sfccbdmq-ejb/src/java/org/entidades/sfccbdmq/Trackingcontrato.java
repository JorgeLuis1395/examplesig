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
 * @author luis
 */
@Entity
@Table(name = "trackingcontrato")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Trackingcontrato.findAll", query = "SELECT t FROM Trackingcontrato t")
    , @NamedQuery(name = "Trackingcontrato.findById", query = "SELECT t FROM Trackingcontrato t WHERE t.id = :id")
    , @NamedQuery(name = "Trackingcontrato.findByAdministradorcontrato", query = "SELECT t FROM Trackingcontrato t WHERE t.administradorcontrato = :administradorcontrato")
    , @NamedQuery(name = "Trackingcontrato.findByFecha", query = "SELECT t FROM Trackingcontrato t WHERE t.fecha = :fecha")
    , @NamedQuery(name = "Trackingcontrato.findByUsuario", query = "SELECT t FROM Trackingcontrato t WHERE t.usuario = :usuario")})
public class Trackingcontrato implements Serializable {

    @Size(max = 2147483647)
    @Column(name = "observacion")
    private String observacion;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "administradorcontrato")
    private String administradorcontrato;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Size(max = 2147483647)
    @Column(name = "usuario")
    private String usuario;
    @JoinColumn(name = "contrato", referencedColumnName = "id")
    @ManyToOne
    private Contratos contrato;

    public Trackingcontrato() {
    }

    public Trackingcontrato(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAdministradorcontrato() {
        return administradorcontrato;
    }

    public void setAdministradorcontrato(String administradorcontrato) {
        this.administradorcontrato = administradorcontrato;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Contratos getContrato() {
        return contrato;
    }

    public void setContrato(Contratos contrato) {
        this.contrato = contrato;
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
        if (!(object instanceof Trackingcontrato)) {
            return false;
        }
        Trackingcontrato other = (Trackingcontrato) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.sfccbdmq.Trackingcontrato[ id=" + id + " ]";
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
    
}
