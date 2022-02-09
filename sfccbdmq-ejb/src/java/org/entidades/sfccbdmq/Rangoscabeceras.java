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
@Table(name = "rangoscabeceras")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Rangoscabeceras.findAll", query = "SELECT r FROM Rangoscabeceras r"),
    @NamedQuery(name = "Rangoscabeceras.findById", query = "SELECT r FROM Rangoscabeceras r WHERE r.id = :id"),
    @NamedQuery(name = "Rangoscabeceras.findByMinimo", query = "SELECT r FROM Rangoscabeceras r WHERE r.minimo = :minimo"),
    @NamedQuery(name = "Rangoscabeceras.findByMaximo", query = "SELECT r FROM Rangoscabeceras r WHERE r.maximo = :maximo"),
    @NamedQuery(name = "Rangoscabeceras.findByTexto", query = "SELECT r FROM Rangoscabeceras r WHERE r.texto = :texto"),
    @NamedQuery(name = "Rangoscabeceras.findByRiesgo", query = "SELECT r FROM Rangoscabeceras r WHERE r.riesgo = :riesgo"),
    @NamedQuery(name = "Rangoscabeceras.findByPorcentaje", query = "SELECT r FROM Rangoscabeceras r WHERE r.porcentaje = :porcentaje")})
public class Rangoscabeceras implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "minimo")
    private BigDecimal minimo;
    @Column(name = "maximo")
    private BigDecimal maximo;
    @Size(max = 2147483647)
    @Column(name = "texto")
    private String texto;
    @Size(max = 2147483647)
    @Column(name = "riesgo")
    private String riesgo;
    @Column(name = "porcentaje")
    private BigDecimal porcentaje;
    @JoinColumn(name = "cabecera", referencedColumnName = "id")
    @ManyToOne
    private Cabecerasrrhh cabecera;

    public Rangoscabeceras() {
    }

    public Rangoscabeceras(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getMinimo() {
        return minimo;
    }

    public void setMinimo(BigDecimal minimo) {
        this.minimo = minimo;
    }

    public BigDecimal getMaximo() {
        return maximo;
    }

    public void setMaximo(BigDecimal maximo) {
        this.maximo = maximo;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getRiesgo() {
        return riesgo;
    }

    public void setRiesgo(String riesgo) {
        this.riesgo = riesgo;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }

    public Cabecerasrrhh getCabecera() {
        return cabecera;
    }

    public void setCabecera(Cabecerasrrhh cabecera) {
        this.cabecera = cabecera;
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
        if (!(object instanceof Rangoscabeceras)) {
            return false;
        }
        Rangoscabeceras other = (Rangoscabeceras) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return texto;
    }
    
}
