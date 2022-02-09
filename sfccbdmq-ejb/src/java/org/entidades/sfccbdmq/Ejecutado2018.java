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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author luis
 */
@Entity
@Table(name = "ejecutado2018")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Ejecutado2018.findAll", query = "SELECT e FROM Ejecutado2018 e"),
    @NamedQuery(name = "Ejecutado2018.findById", query = "SELECT e FROM Ejecutado2018 e WHERE e.id = :id"),
    @NamedQuery(name = "Ejecutado2018.findByProyecto", query = "SELECT e FROM Ejecutado2018 e WHERE e.proyecto = :proyecto"),
    @NamedQuery(name = "Ejecutado2018.findByPartida", query = "SELECT e FROM Ejecutado2018 e WHERE e.partida = :partida"),
    @NamedQuery(name = "Ejecutado2018.findByValor", query = "SELECT e FROM Ejecutado2018 e WHERE e.valor = :valor")})
public class Ejecutado2018 implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "proyecto")
    private String proyecto;
    @Size(max = 2147483647)
    @Column(name = "partida")
    private String partida;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;

    public Ejecutado2018() {
    }

    public Ejecutado2018(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProyecto() {
        return proyecto;
    }

    public void setProyecto(String proyecto) {
        this.proyecto = proyecto;
    }

    public String getPartida() {
        return partida;
    }

    public void setPartida(String partida) {
        this.partida = partida;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
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
        if (!(object instanceof Ejecutado2018)) {
            return false;
        }
        Ejecutado2018 other = (Ejecutado2018) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.sfccbdmq.Ejecutado2018[ id=" + id + " ]";
    }
    
}
