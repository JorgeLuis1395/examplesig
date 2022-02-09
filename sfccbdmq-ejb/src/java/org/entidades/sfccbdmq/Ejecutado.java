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
 * @author edwin
 */
@Entity
@Table(name = "ejecutado")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Ejecutado.findAll", query = "SELECT e FROM Ejecutado e")
    , @NamedQuery(name = "Ejecutado.findById", query = "SELECT e FROM Ejecutado e WHERE e.id = :id")
    , @NamedQuery(name = "Ejecutado.findByFecha", query = "SELECT e FROM Ejecutado e WHERE e.fecha = :fecha")
    , @NamedQuery(name = "Ejecutado.findByValor", query = "SELECT e FROM Ejecutado e WHERE e.valor = :valor")
    , @NamedQuery(name = "Ejecutado.findByEjecutado", query = "SELECT e FROM Ejecutado e WHERE e.ejecutado = :ejecutado")})
public class Ejecutado implements Serializable {

    @JoinColumn(name = "kardexbanco", referencedColumnName = "id")
    @ManyToOne
    private Kardexbanco kardexbanco;

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
    @Column(name = "ejecutado")
    private Boolean ejecutado;
    @JoinColumn(name = "detallecomrpomiso", referencedColumnName = "id")
    @ManyToOne
    private Detallecompromiso detallecomrpomiso;

    public Ejecutado() {
    }

    public Ejecutado(Integer id) {
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
        this.valor = valor;
    }

    public Boolean getEjecutado() {
        return ejecutado;
    }

    public void setEjecutado(Boolean ejecutado) {
        this.ejecutado = ejecutado;
    }

    public Detallecompromiso getDetallecomrpomiso() {
        return detallecomrpomiso;
    }

    public void setDetallecomrpomiso(Detallecompromiso detallecomrpomiso) {
        this.detallecomrpomiso = detallecomrpomiso;
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
        if (!(object instanceof Ejecutado)) {
            return false;
        }
        Ejecutado other = (Ejecutado) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.sfccbdmq.Ejecutado[ id=" + id + " ]";
    }

    public Kardexbanco getKardexbanco() {
        return kardexbanco;
    }

    public void setKardexbanco(Kardexbanco kardexbanco) {
        this.kardexbanco = kardexbanco;
    }
    
}