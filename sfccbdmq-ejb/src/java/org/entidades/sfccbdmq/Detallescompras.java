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
@Table(name = "detallescompras")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Detallescompras.findAll", query = "SELECT d FROM Detallescompras d")
    , @NamedQuery(name = "Detallescompras.findById", query = "SELECT d FROM Detallescompras d WHERE d.id = :id")
    , @NamedQuery(name = "Detallescompras.findByValor", query = "SELECT d FROM Detallescompras d WHERE d.valor = :valor")})
public class Detallescompras implements Serializable {

    @Column(name = "fecha_ejecucion")
    @Temporal(TemporalType.DATE)
    private Date fechaEjecucion;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    @JoinColumn(name = "cabeceradoc", referencedColumnName = "id")
    @ManyToOne
    private Cabdocelect cabeceradoc;
    @JoinColumn(name = "cuenta", referencedColumnName = "id")
    @ManyToOne
    private Cuentas cuenta;
    @JoinColumn(name = "detallecompromiso", referencedColumnName = "id")
    @ManyToOne
    private Detallecompromiso detallecompromiso;
    @Transient
    private double valorAnterior;

    public Detallescompras() {
    }

    public Detallescompras(Integer id) {
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
       if (valor != null) {
            double cuadre = Math.round(valor.doubleValue() * 100);
            double dividido = cuadre / 100;
            valor = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
        }
        this.valor = valor;
    }

    public Cabdocelect getCabeceradoc() {
        return cabeceradoc;
    }

    public void setCabeceradoc(Cabdocelect cabeceradoc) {
        this.cabeceradoc = cabeceradoc;
    }

    public Cuentas getCuenta() {
        return cuenta;
    }

    public void setCuenta(Cuentas cuenta) {
        this.cuenta = cuenta;
    }

    public Detallecompromiso getDetallecompromiso() {
        return detallecompromiso;
    }

    public void setDetallecompromiso(Detallecompromiso detallecompromiso) {
        this.detallecompromiso = detallecompromiso;
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
        if (!(object instanceof Detallescompras)) {
            return false;
        }
        Detallescompras other = (Detallescompras) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.sfccbdmq.Detallescompras[ id=" + id + " ]";
    }

    /**
     * @return the valorAnterior
     */
    public double getValorAnterior() {
        return valorAnterior;
    }

    /**
     * @param valorAnterior the valorAnterior to set
     */
    public void setValorAnterior(double valorAnterior) {
//       if (valorAnterior != null) {
        double cuadre = Math.round(valorAnterior * 100);
        double dividido = cuadre / 100;
        this.valorAnterior = dividido;

//        }
        this.valorAnterior = valorAnterior;
    }

    public Date getFechaEjecucion() {
        return fechaEjecucion;
    }

    public void setFechaEjecucion(Date fechaEjecucion) {
        this.fechaEjecucion = fechaEjecucion;
    }

}
