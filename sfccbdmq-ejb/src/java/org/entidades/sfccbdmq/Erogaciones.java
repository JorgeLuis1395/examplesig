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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "erogaciones")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Erogaciones.findAll", query = "SELECT e FROM Erogaciones e"),
    @NamedQuery(name = "Erogaciones.findById", query = "SELECT e FROM Erogaciones e WHERE e.id = :id"),
    @NamedQuery(name = "Erogaciones.findByValor", query = "SELECT e FROM Erogaciones e WHERE e.valor = :valor"),
    @NamedQuery(name = "Erogaciones.findByVidautil", query = "SELECT e FROM Erogaciones e WHERE e.vidautil = :vidautil"),
    @NamedQuery(name = "Erogaciones.findByDescripcion", query = "SELECT e FROM Erogaciones e WHERE e.descripcion = :descripcion"),
    @NamedQuery(name = "Erogaciones.findByVidautilactivo", query = "SELECT e FROM Erogaciones e WHERE e.vidautilactivo = :vidautilactivo"),
    @NamedQuery(name = "Erogaciones.findByValoractivo", query = "SELECT e FROM Erogaciones e WHERE e.valoractivo = :valoractivo")})
public class Erogaciones implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    @Column(name = "vidautil")
    private Integer vidautil;
    @Size(max = 2147483647)
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "vidautilactivo")
    private Integer vidautilactivo;
    @Column(name = "valoractivo")
    private BigDecimal valoractivo;
    @JoinColumn(name = "activo", referencedColumnName = "id")
    @ManyToOne
    private Activos activo;

    public Erogaciones() {
    }

    public Erogaciones(Integer id) {
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

    public Integer getVidautil() {
        return vidautil;
    }

    public void setVidautil(Integer vidautil) {
        this.vidautil = vidautil;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getVidautilactivo() {
        return vidautilactivo;
    }

    public void setVidautilactivo(Integer vidautilactivo) {
        this.vidautilactivo = vidautilactivo;
    }

    public BigDecimal getValoractivo() {
        return valoractivo;
    }

    public void setValoractivo(BigDecimal valoractivo) {
        this.valoractivo = valoractivo;
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
        if (!(object instanceof Erogaciones)) {
            return false;
        }
        Erogaciones other = (Erogaciones) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return descripcion;
    }
    
}
