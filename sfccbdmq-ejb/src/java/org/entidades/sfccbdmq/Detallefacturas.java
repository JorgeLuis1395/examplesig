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
 * @author luis
 */
@Entity
@Table(name = "detallefacturas")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Detallefacturas.findAll", query = "SELECT d FROM Detallefacturas d")
    , @NamedQuery(name = "Detallefacturas.findById", query = "SELECT d FROM Detallefacturas d WHERE d.id = :id")
    , @NamedQuery(name = "Detallefacturas.findByCantidad", query = "SELECT d FROM Detallefacturas d WHERE d.cantidad = :cantidad")
    , @NamedQuery(name = "Detallefacturas.findByDescuento", query = "SELECT d FROM Detallefacturas d WHERE d.descuento = :descuento")
    , @NamedQuery(name = "Detallefacturas.findByValorimpuesto", query = "SELECT d FROM Detallefacturas d WHERE d.valorimpuesto = :valorimpuesto")
    , @NamedQuery(name = "Detallefacturas.findByValor", query = "SELECT d FROM Detallefacturas d WHERE d.valor = :valor")})
public class Detallefacturas implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "cantidad")
    private Integer cantidad;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "descuento")
    private BigDecimal descuento;
    @Column(name = "valorimpuesto")
    private BigDecimal valorimpuesto;
    @Column(name = "valor")
    private BigDecimal valor;
    @JoinColumn(name = "asignacion", referencedColumnName = "id")
    @ManyToOne
    private Asignaciones asignacion;
    @JoinColumn(name = "factura", referencedColumnName = "id")
    @ManyToOne
    private Cabecerafacturas factura;
    @JoinColumn(name = "impuesto", referencedColumnName = "id")
    @ManyToOne
    private Impuestos impuesto;
    @JoinColumn(name = "producto", referencedColumnName = "id")
    @ManyToOne
    private Productos producto;

    public Detallefacturas() {
    }

    public Detallefacturas(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public BigDecimal getValorimpuesto() {
        return valorimpuesto;
    }

    public void setValorimpuesto(BigDecimal valorimpuesto) {
        this.valorimpuesto = valorimpuesto;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Asignaciones getAsignacion() {
        return asignacion;
    }

    public void setAsignacion(Asignaciones asignacion) {
        this.asignacion = asignacion;
    }

    public Cabecerafacturas getFactura() {
        return factura;
    }

    public void setFactura(Cabecerafacturas factura) {
        this.factura = factura;
    }

    public Impuestos getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(Impuestos impuesto) {
        this.impuesto = impuesto;
    }

    public Productos getProducto() {
        return producto;
    }

    public void setProducto(Productos producto) {
        this.producto = producto;
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
        if (!(object instanceof Detallefacturas)) {
            return false;
        }
        Detallefacturas other = (Detallefacturas) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.sfccbdmq.Detallefacturas[ id=" + id + " ]";
    }
    
}
