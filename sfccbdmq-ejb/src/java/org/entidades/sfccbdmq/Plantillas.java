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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author luis
 */
@Entity
@Table(name = "plantillas")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Plantillas.findAll", query = "SELECT p FROM Plantillas p")
    , @NamedQuery(name = "Plantillas.findById", query = "SELECT p FROM Plantillas p WHERE p.id = :id")
    , @NamedQuery(name = "Plantillas.findByCodigo", query = "SELECT p FROM Plantillas p WHERE p.codigo = :codigo")
    , @NamedQuery(name = "Plantillas.findByCantidad", query = "SELECT p FROM Plantillas p WHERE p.cantidad = :cantidad")})
public class Plantillas implements Serializable {

    @Column(name = "cantidadinv")
    private Integer cantidadinv;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "codigo")
    private String codigo;
    @Column(name = "cantidad")
    private Integer cantidad;
    @JoinColumn(name = "tipo", referencedColumnName = "id")
    @ManyToOne
    private Codigos tipo;

    public Plantillas() {
    }

    public Plantillas(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Codigos getTipo() {
        return tipo;
    }

    public void setTipo(Codigos tipo) {
        this.tipo = tipo;
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
        if (!(object instanceof Plantillas)) {
            return false;
        }
        Plantillas other = (Plantillas) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.sfccbdmq.Plantillas[ id=" + id + " ]";
    }

    public Integer getCantidadinv() {
        return cantidadinv;
    }

    public void setCantidadinv(Integer cantidadinv) {
        this.cantidadinv = cantidadinv;
    }
    
}
