/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
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
@Table(name = "vidasutiles")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Vidasutiles.findAll", query = "SELECT v FROM Vidasutiles v"),
    @NamedQuery(name = "Vidasutiles.findById", query = "SELECT v FROM Vidasutiles v WHERE v.id = :id"),
    @NamedQuery(name = "Vidasutiles.findByVidautil", query = "SELECT v FROM Vidasutiles v WHERE v.vidautil = :vidautil"),
    @NamedQuery(name = "Vidasutiles.findByValorresidual", query = "SELECT v FROM Vidasutiles v WHERE v.valorresidual = :valorresidual"),
    @NamedQuery(name = "Vidasutiles.findByUnidades", query = "SELECT v FROM Vidasutiles v WHERE v.unidades = :unidades"),
    @NamedQuery(name = "Vidasutiles.findByAnio", query = "SELECT v FROM Vidasutiles v WHERE v.anio = :anio"),
    @NamedQuery(name = "Vidasutiles.findByMes", query = "SELECT v FROM Vidasutiles v WHERE v.mes = :mes")})
public class Vidasutiles implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "vidautil")
    private Integer vidautil;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valorresidual")
    private Float valorresidual;
    @Column(name = "unidades")
    private Float unidades;
    @Column(name = "anio")
    private Integer anio;
    @Column(name = "mes")
    private Integer mes;
    @JoinColumn(name = "activo", referencedColumnName = "id")
    @ManyToOne
    private Activos activo;

    public Vidasutiles() {
    }

    public Vidasutiles(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVidautil() {
        return vidautil;
    }

    public void setVidautil(Integer vidautil) {
        this.vidautil = vidautil;
    }

    public Float getValorresidual() {
        return valorresidual;
    }

    public void setValorresidual(Float valorresidual) {
        this.valorresidual = valorresidual;
    }

    public Float getUnidades() {
        return unidades;
    }

    public void setUnidades(Float unidades) {
        this.unidades = unidades;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
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
        if (!(object instanceof Vidasutiles)) {
            return false;
        }
        Vidasutiles other = (Vidasutiles) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Vidasutiles[ id=" + id + " ]";
    }
    
}
