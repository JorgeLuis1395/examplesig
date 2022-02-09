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
@Table(name = "detallesfondoexterior")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Detallesfondoexterior.findAll", query = "SELECT d FROM Detallesfondoexterior d"),
    @NamedQuery(name = "Detallesfondoexterior.findById", query = "SELECT d FROM Detallesfondoexterior d WHERE d.id = :id"),
    @NamedQuery(name = "Detallesfondoexterior.findByValor", query = "SELECT d FROM Detallesfondoexterior d WHERE d.valor = :valor"),
    @NamedQuery(name = "Detallesfondoexterior.findByFechaEjecucion", query = "SELECT d FROM Detallesfondoexterior d WHERE d.fechaEjecucion = :fechaEjecucion")})
public class Detallesfondoexterior implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    @Column(name = "fecha_ejecucion")
    @Temporal(TemporalType.DATE)
    private Date fechaEjecucion;
    @JoinColumn(name = "cuenta", referencedColumnName = "id")
    @ManyToOne
    private Cuentas cuenta;
    @JoinColumn(name = "detallecertificacion", referencedColumnName = "id")
    @ManyToOne
    private Detallecertificaciones detallecertificacion;
    @JoinColumn(name = "detallecompromiso", referencedColumnName = "id")
    @ManyToOne
    private Detallecompromiso detallecompromiso;
    @JoinColumn(name = "vale", referencedColumnName = "id")
    @ManyToOne
    private Valesfondos vale;

    public Detallesfondoexterior() {
    }

    public Detallesfondoexterior(Integer id) {
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

    public Date getFechaEjecucion() {
        return fechaEjecucion;
    }

    public void setFechaEjecucion(Date fechaEjecucion) {
        this.fechaEjecucion = fechaEjecucion;
    }

    public Cuentas getCuenta() {
        return cuenta;
    }

    public void setCuenta(Cuentas cuenta) {
        this.cuenta = cuenta;
    }

    public Detallecertificaciones getDetallecertificacion() {
        return detallecertificacion;
    }

    public void setDetallecertificacion(Detallecertificaciones detallecertificacion) {
        this.detallecertificacion = detallecertificacion;
    }

    public Detallecompromiso getDetallecompromiso() {
        return detallecompromiso;
    }

    public void setDetallecompromiso(Detallecompromiso detallecompromiso) {
        this.detallecompromiso = detallecompromiso;
    }

    public Valesfondos getVale() {
        return vale;
    }

    public void setVale(Valesfondos vale) {
        this.vale = vale;
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
        if (!(object instanceof Detallesfondoexterior)) {
            return false;
        }
        Detallesfondoexterior other = (Detallesfondoexterior) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.sfccbdmq.Detallesfondoexterior[ id=" + id + " ]";
    }
    
}
