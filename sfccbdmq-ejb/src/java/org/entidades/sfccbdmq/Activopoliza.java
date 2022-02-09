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
@Table(name = "activopoliza")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Activopoliza.findAll", query = "SELECT a FROM Activopoliza a"),
    @NamedQuery(name = "Activopoliza.findById", query = "SELECT a FROM Activopoliza a WHERE a.id = :id"),
    @NamedQuery(name = "Activopoliza.findByValorasegurado", query = "SELECT a FROM Activopoliza a WHERE a.valorasegurado = :valorasegurado"),
    @NamedQuery(name = "Activopoliza.findByDesde", query = "SELECT a FROM Activopoliza a WHERE a.desde = :desde"),
    @NamedQuery(name = "Activopoliza.findByHasta", query = "SELECT a FROM Activopoliza a WHERE a.hasta = :hasta")})
public class Activopoliza implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valorasegurado")
    private BigDecimal valorasegurado;
    @Column(name = "desde")
    @Temporal(TemporalType.DATE)
    private Date desde;
    @Column(name = "hasta")
    @Temporal(TemporalType.DATE)
    private Date hasta;
    @JoinColumn(name = "poliza", referencedColumnName = "id")
    @ManyToOne
    private Polizas poliza;
    @JoinColumn(name = "activo", referencedColumnName = "id")
    @ManyToOne
    private Activos activo;

    public Activopoliza() {
    }

    public Activopoliza(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getValorasegurado() {
        return valorasegurado;
    }

    public void setValorasegurado(BigDecimal valorasegurado) {
        this.valorasegurado = valorasegurado;
    }

    public Date getDesde() {
        return desde;
    }

    public void setDesde(Date desde) {
        this.desde = desde;
    }

    public Date getHasta() {
        return hasta;
    }

    public void setHasta(Date hasta) {
        this.hasta = hasta;
    }

    public Polizas getPoliza() {
        return poliza;
    }

    public void setPoliza(Polizas poliza) {
        this.poliza = poliza;
    }

    public Activos getActivo() {
        return activo;
    }

    public void setActivo(Activos activo) {
        this.activo = activo;
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
        if (!(object instanceof Activopoliza)) {
            return false;
        }
        Activopoliza other = (Activopoliza) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Activopoliza[ id=" + id + " ]";
    }
    
}
