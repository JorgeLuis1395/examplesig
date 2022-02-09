/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "reformas")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Reformas.findAll", query = "SELECT r FROM Reformas r"),
    @NamedQuery(name = "Reformas.findById", query = "SELECT r FROM Reformas r WHERE r.id = :id"),
    @NamedQuery(name = "Reformas.findByFecha", query = "SELECT r FROM Reformas r WHERE r.fecha = :fecha"),
    @NamedQuery(name = "Reformas.findByValor", query = "SELECT r FROM Reformas r WHERE r.valor = :valor")})
public class Reformas implements Serializable {
    @JoinColumn(name = "cabecera", referencedColumnName = "id")
    @ManyToOne
    private Cabecerareformas cabecera;
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
    private BigDecimal valor;
    @JoinColumn(name = "asignacion", referencedColumnName = "id")
    @ManyToOne
    private Asignaciones asignacion;
    @Transient
    private double totalAisgnacion;
    @Transient
    private double totalReforma;
    @Transient
    private double totalTotal;
    public Reformas() {
    }

    public Reformas(Integer id) {
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

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        if (valor != null) {
            double cuadre = Math.round(valor.doubleValue() * 100);
            double dividido = cuadre / 100;
            valor = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
        }
        this.valor = valor;
    }

    public Asignaciones getAsignacion() {
        return asignacion;
    }

    public void setAsignacion(Asignaciones asignacion) {
        this.asignacion = asignacion;
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
        if (!(object instanceof Reformas)) {
            return false;
        }
        Reformas other = (Reformas) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Reformas[ id=" + id + " ]";
    }

    public Cabecerareformas getCabecera() {
        return cabecera;
    }

    public void setCabecera(Cabecerareformas cabecera) {
        this.cabecera = cabecera;
    }

    /**
     * @return the totalAisgnacion
     */
    public double getTotalAisgnacion() {
        return totalAisgnacion;
    }

    /**
     * @param totalAisgnacion the totalAisgnacion to set
     */
    public void setTotalAisgnacion(double totalAisgnacion) {
        this.totalAisgnacion = totalAisgnacion;
    }

    /**
     * @return the totalReforma
     */
    public double getTotalReforma() {
        return totalReforma;
    }

    /**
     * @param totalReforma the totalReforma to set
     */
    public void setTotalReforma(double totalReforma) {
        this.totalReforma = totalReforma;
    }

    /**
     * @return the totalTotal
     */
    public double getTotalTotal() {
        return totalTotal;
    }

    /**
     * @param totalTotal the totalTotal to set
     */
    public void setTotalTotal(double totalTotal) {
        this.totalTotal = totalTotal;
    }
    
}
