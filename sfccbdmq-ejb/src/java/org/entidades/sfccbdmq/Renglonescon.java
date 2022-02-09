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

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "renglonescon")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Renglonescon.findAll", query = "SELECT r FROM Renglonescon r")
    , @NamedQuery(name = "Renglonescon.findById", query = "SELECT r FROM Renglonescon r WHERE r.id = :id")
    , @NamedQuery(name = "Renglonescon.findByReferencia", query = "SELECT r FROM Renglonescon r WHERE r.referencia = :referencia")
    , @NamedQuery(name = "Renglonescon.findByValor", query = "SELECT r FROM Renglonescon r WHERE r.valor = :valor")
    , @NamedQuery(name = "Renglonescon.findByFecha", query = "SELECT r FROM Renglonescon r WHERE r.fecha = :fecha")})
public class Renglonescon implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "referencia")
    private String referencia;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @JoinColumn(name = "conciliacion", referencedColumnName = "id")
    @ManyToOne
    private Conciliaciones conciliacion;
    @JoinColumn(name = "detalleconciliacion", referencedColumnName = "id")
    @ManyToOne
    private Detalleconciliaciones detalleconciliacion;
    @Column(name = "conciliado")
    private Boolean conciliado;

    public Renglonescon() {
    }

    public Renglonescon(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Conciliaciones getConciliacion() {
        return conciliacion;
    }

    public void setConciliacion(Conciliaciones conciliacion) {
        this.conciliacion = conciliacion;
    }

    public Detalleconciliaciones getDetalleconciliacion() {
        return detalleconciliacion;
    }

    public void setDetalleconciliacion(Detalleconciliaciones detalleconciliacion) {
        this.detalleconciliacion = detalleconciliacion;
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
        if (!(object instanceof Renglonescon)) {
            return false;
        }
        Renglonescon other = (Renglonescon) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.sfccbdmq.Renglonescon[ id=" + id + " ]";
    }

    /**
     * @return the conciliado
     */
    public Boolean getConciliado() {
        return conciliado;
    }

    /**
     * @param conciliado the conciliado to set
     */
    public void setConciliado(Boolean conciliado) {
        this.conciliado = conciliado;
    }

}
