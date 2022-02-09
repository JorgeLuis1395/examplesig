/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
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
 * @author luis
 */
@Entity
@Table(name = "detallecertificacionespoa")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Detallecertificacionespoa.findAll", query = "SELECT d FROM Detallecertificacionespoa d")
    , @NamedQuery(name = "Detallecertificacionespoa.findById", query = "SELECT d FROM Detallecertificacionespoa d WHERE d.id = :id")
    , @NamedQuery(name = "Detallecertificacionespoa.findByValor", query = "SELECT d FROM Detallecertificacionespoa d WHERE d.valor = :valor")
    , @NamedQuery(name = "Detallecertificacionespoa.findByAdjudicado", query = "SELECT d FROM Detallecertificacionespoa d WHERE d.adjudicado = :adjudicado")})
public class Detallecertificacionespoa implements Serializable {

    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    @Column(name = "adjudicado")
    private BigDecimal adjudicado;
    @JoinColumn(name = "asignacion", referencedColumnName = "id")
    @ManyToOne
    private Asignacionespoa asignacion;
    @JoinColumn(name = "certificacion", referencedColumnName = "id")
    @ManyToOne
    private Certificacionespoa certificacion;

    public Detallecertificacionespoa() {
    }

    public Detallecertificacionespoa(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getAdjudicado() {
        return adjudicado;
    }

    public void setAdjudicado(BigDecimal adjudicado) {
        this.adjudicado = adjudicado;
    }

    public Asignacionespoa getAsignacion() {
        return asignacion;
    }

    public void setAsignacion(Asignacionespoa asignacion) {
        this.asignacion = asignacion;
    }

    public Certificacionespoa getCertificacion() {
        return certificacion;
    }

    public void setCertificacion(Certificacionespoa certificacion) {
        this.certificacion = certificacion;
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
        if (!(object instanceof Detallecertificacionespoa)) {
            return false;
        }
        Detallecertificacionespoa other = (Detallecertificacionespoa) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.pacpoaiepi.Detallecertificacionespoa[ id=" + id + " ]";
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    
}
