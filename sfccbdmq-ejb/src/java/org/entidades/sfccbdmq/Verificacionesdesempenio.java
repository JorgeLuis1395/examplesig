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
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "verificacionesdesempenio")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Verificacionesdesempenio.findAll", query = "SELECT v FROM Verificacionesdesempenio v"),
    @NamedQuery(name = "Verificacionesdesempenio.findById", query = "SELECT v FROM Verificacionesdesempenio v WHERE v.id = :id"),
    @NamedQuery(name = "Verificacionesdesempenio.findByValor", query = "SELECT v FROM Verificacionesdesempenio v WHERE v.valor = :valor"),
    @NamedQuery(name = "Verificacionesdesempenio.findByActivo", query = "SELECT v FROM Verificacionesdesempenio v WHERE v.activo = :activo")})
public class Verificacionesdesempenio implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    @Column(name = "activo")
    private Boolean activo;
    @JoinColumn(name = "valoracion", referencedColumnName = "id")
    @ManyToOne
    private Valoracionesdesempenio valoracion;
    @JoinColumn(name = "experiencia", referencedColumnName = "id")
    @ManyToOne
    private Experiencias experiencia;
    @Transient
    private BigDecimal nota;
    public Verificacionesdesempenio() {
    }

    public Verificacionesdesempenio(Integer id) {
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

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Valoracionesdesempenio getValoracion() {
        return valoracion;
    }

    public void setValoracion(Valoracionesdesempenio valoracion) {
        this.valoracion = valoracion;
    }

    public Experiencias getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(Experiencias experiencia) {
        this.experiencia = experiencia;
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
        if (!(object instanceof Verificacionesdesempenio)) {
            return false;
        }
        Verificacionesdesempenio other = (Verificacionesdesempenio) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Verificacionesdesempenio[ id=" + id + " ]";
    }

    /**
     * @return the nota
     */
    public BigDecimal getNota() {
        return nota;
    }

    /**
     * @param nota the nota to set
     */
    public void setNota(BigDecimal nota) {
        this.nota = nota;
    }
    
}
