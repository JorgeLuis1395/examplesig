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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "detalleamortiz")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Detalleamortiz.findAll", query = "SELECT d FROM Detalleamortiz d"),
    @NamedQuery(name = "Detalleamortiz.findById", query = "SELECT d FROM Detalleamortiz d WHERE d.id = :id"),
    @NamedQuery(name = "Detalleamortiz.findByFecha", query = "SELECT d FROM Detalleamortiz d WHERE d.fecha = :fecha"),
    @NamedQuery(name = "Detalleamortiz.findByValor", query = "SELECT d FROM Detalleamortiz d WHERE d.valor = :valor"),
    @NamedQuery(name = "Detalleamortiz.findByContabilizado", query = "SELECT d FROM Detalleamortiz d WHERE d.contabilizado = :contabilizado")})
public class Detalleamortiz implements Serializable {

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
    @Column(name = "contabilizado")
    @Temporal(TemporalType.DATE)
    private Date contabilizado;
    @JoinColumn(name = "amortizcontab", referencedColumnName = "id")
    @ManyToOne
    private Amortizcontables amortizcontab;

    public Detalleamortiz() {
    }

    public Detalleamortiz(Integer id) {
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

    public Date getContabilizado() {
        return contabilizado;
    }

    public void setContabilizado(Date contabilizado) {
        this.contabilizado = contabilizado;
    }

    public Amortizcontables getAmortizcontab() {
        return amortizcontab;
    }

    public void setAmortizcontab(Amortizcontables amortizcontab) {
        this.amortizcontab = amortizcontab;
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
        if (!(object instanceof Detalleamortiz)) {
            return false;
        }
        Detalleamortiz other = (Detalleamortiz) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Detalleamortiz[ id=" + id + " ]";
    }

}
