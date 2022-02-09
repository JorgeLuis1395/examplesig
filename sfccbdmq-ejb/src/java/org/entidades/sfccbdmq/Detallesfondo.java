/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
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
@Table(name = "detallesfondo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Detallesfondo.findAll", query = "SELECT d FROM Detallesfondo d")
    , @NamedQuery(name = "Detallesfondo.findById", query = "SELECT d FROM Detallesfondo d WHERE d.id = :id")
    , @NamedQuery(name = "Detallesfondo.findByValor", query = "SELECT d FROM Detallesfondo d WHERE d.valor = :valor")})
public class Detallesfondo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
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

    public Detallesfondo() {
    }

    public Detallesfondo(Integer id) {
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
        if (!(object instanceof Detallesfondo)) {
            return false;
        }
        Detallesfondo other = (Detallesfondo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.sfccbdmq.Detallesfondo[ id=" + id + " ]";
    }
    
}
