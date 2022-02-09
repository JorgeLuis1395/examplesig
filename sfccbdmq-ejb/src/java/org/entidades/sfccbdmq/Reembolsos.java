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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import org.entidades.sfccbdmq.Obligaciones;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "reembolsos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Reembolsos.findAll", query = "SELECT r FROM Reembolsos r")
    , @NamedQuery(name = "Reembolsos.findById", query = "SELECT r FROM Reembolsos r WHERE r.id = :id")
    , @NamedQuery(name = "Reembolsos.findByRuc", query = "SELECT r FROM Reembolsos r WHERE r.ruc = :ruc")
    , @NamedQuery(name = "Reembolsos.findByEstablecimiento", query = "SELECT r FROM Reembolsos r WHERE r.establecimiento = :establecimiento")
    , @NamedQuery(name = "Reembolsos.findByPunto", query = "SELECT r FROM Reembolsos r WHERE r.punto = :punto")
    , @NamedQuery(name = "Reembolsos.findByNumero", query = "SELECT r FROM Reembolsos r WHERE r.numero = :numero")
    , @NamedQuery(name = "Reembolsos.findByBaseimponible", query = "SELECT r FROM Reembolsos r WHERE r.baseimponible = :baseimponible")})
public class Reembolsos implements Serializable {

    @Size(max = 2147483647)
    @Column(name = "autorizacion")
    private String autorizacion;
    @Column(name = "fechaemision")
    @Temporal(TemporalType.DATE)
    private Date fechaemision;
    @Column(name = "baseimponible0")
    private BigDecimal baseimponible0;
    @Column(name = "valoriva")
    private BigDecimal valoriva;
    @JoinColumn(name = "tipo", referencedColumnName = "id")
    @ManyToOne
    private Codigos tipo;

    @JoinColumn(name = "comprobante", referencedColumnName = "id")
    @ManyToOne
    private Codigos comprobante;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "ruc")
    private String ruc;
    @Size(max = 2147483647)
    @Column(name = "establecimiento")
    private String establecimiento;
    @Size(max = 2147483647)
    @Column(name = "punto")
    private String punto;
    @Size(max = 2147483647)
    @Column(name = "numero")
    private String numero;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "baseimponible")
    private BigDecimal baseimponible;
    @JoinColumn(name = "obligacion", referencedColumnName = "id")
    @ManyToOne
    private Obligaciones obligacion;

    public Reembolsos() {
    }

    public Reembolsos(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getEstablecimiento() {
        return establecimiento;
    }

    public void setEstablecimiento(String establecimiento) {
        this.establecimiento = establecimiento;
    }

    public String getPunto() {
        return punto;
    }

    public void setPunto(String punto) {
        this.punto = punto;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public BigDecimal getBaseimponible() {
        return baseimponible;
    }

    public void setBaseimponible(BigDecimal baseimponible) {
        this.baseimponible = baseimponible;
    }

    public Obligaciones getObligacion() {
        return obligacion;
    }

    public void setObligacion(Obligaciones obligacion) {
        this.obligacion = obligacion;
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
        if (!(object instanceof Reembolsos)) {
            return false;
        }
        Reembolsos other = (Reembolsos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.beans.sfccbdmq.Reembolsos[ id=" + id + " ]";
    }

    public Codigos getComprobante() {
        return comprobante;
    }

    public void setComprobante(Codigos comprobante) {
        this.comprobante = comprobante;
    }

    public String getAutorizacion() {
        return autorizacion;
    }

    public void setAutorizacion(String autorizacion) {
        this.autorizacion = autorizacion;
    }

    public Date getFechaemision() {
        return fechaemision;
    }

    public void setFechaemision(Date fechaemision) {
        this.fechaemision = fechaemision;
    }

    public BigDecimal getBaseimponible0() {
        return baseimponible0;
    }

    public void setBaseimponible0(BigDecimal baseimponible0) {
        this.baseimponible0 = baseimponible0;
    }

    public BigDecimal getValoriva() {
        return valoriva;
    }

    public void setValoriva(BigDecimal valoriva) {
        this.valoriva = valoriva;
    }

    public Codigos getTipo() {
        return tipo;
    }

    public void setTipo(Codigos tipo) {
        this.tipo = tipo;
    }
    
}
