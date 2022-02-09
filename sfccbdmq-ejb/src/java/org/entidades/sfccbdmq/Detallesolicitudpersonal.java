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
@Table(name = "detallesolicitudpersonal")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Detallesolicitudpersonal.findAll", query = "SELECT d FROM Detallesolicitudpersonal d")
    , @NamedQuery(name = "Detallesolicitudpersonal.findById", query = "SELECT d FROM Detallesolicitudpersonal d WHERE d.id = :id")
    , @NamedQuery(name = "Detallesolicitudpersonal.findByTipo", query = "SELECT d FROM Detallesolicitudpersonal d WHERE d.tipo = :tipo")
    , @NamedQuery(name = "Detallesolicitudpersonal.findByDescripcion", query = "SELECT d FROM Detallesolicitudpersonal d WHERE d.descripcion = :descripcion")
    , @NamedQuery(name = "Detallesolicitudpersonal.findByAprobado", query = "SELECT d FROM Detallesolicitudpersonal d WHERE d.aprobado = :aprobado")
    , @NamedQuery(name = "Detallesolicitudpersonal.findByActivo", query = "SELECT d FROM Detallesolicitudpersonal d WHERE d.activo = :activo")
    , @NamedQuery(name = "Detallesolicitudpersonal.findByFechaaprobado", query = "SELECT d FROM Detallesolicitudpersonal d WHERE d.fechaaprobado = :fechaaprobado")
    , @NamedQuery(name = "Detallesolicitudpersonal.findByNumerocertificado", query = "SELECT d FROM Detallesolicitudpersonal d WHERE d.numerocertificado = :numerocertificado")})
public class Detallesolicitudpersonal implements Serializable {

    @Column(name = "mes")
    private Integer mes;
    @Column(name = "anio")
    private Integer anio;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "tipo")
    private Integer tipo;
    @Size(max = 2147483647)
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "aprobado")
    private Boolean aprobado;
    @Column(name = "activo")
    private Boolean activo;
    @Column(name = "fechaaprobado")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaaprobado;
    @Size(max = 2147483647)
    @Column(name = "numerocertificado")
    private String numerocertificado;
    @JoinColumn(name = "solicitudpersonal", referencedColumnName = "id")
    @ManyToOne
    private Solicitudpersonal solicitudpersonal;

    public Detallesolicitudpersonal() {
    }

    public Detallesolicitudpersonal(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getAprobado() {
        return aprobado;
    }

    public void setAprobado(Boolean aprobado) {
        this.aprobado = aprobado;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Date getFechaaprobado() {
        return fechaaprobado;
    }

    public void setFechaaprobado(Date fechaaprobado) {
        this.fechaaprobado = fechaaprobado;
    }

    public String getNumerocertificado() {
        return numerocertificado;
    }

    public void setNumerocertificado(String numerocertificado) {
        this.numerocertificado = numerocertificado;
    }

    public Solicitudpersonal getSolicitudpersonal() {
        return solicitudpersonal;
    }

    public void setSolicitudpersonal(Solicitudpersonal solicitudpersonal) {
        this.solicitudpersonal = solicitudpersonal;
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
        if (!(object instanceof Detallesolicitudpersonal)) {
            return false;
        }
        Detallesolicitudpersonal other = (Detallesolicitudpersonal) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.sfccbdmq.Detallesolicitudpersonal[ id=" + id + " ]";
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }
    
}