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
@Table(name = "tipojubilacion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tipojubilacion.findAll", query = "SELECT t FROM Tipojubilacion t")
    , @NamedQuery(name = "Tipojubilacion.findById", query = "SELECT t FROM Tipojubilacion t WHERE t.id = :id")
    , @NamedQuery(name = "Tipojubilacion.findByNombre", query = "SELECT t FROM Tipojubilacion t WHERE t.nombre = :nombre")
    , @NamedQuery(name = "Tipojubilacion.findByEdadminima", query = "SELECT t FROM Tipojubilacion t WHERE t.edadminima = :edadminima")
    , @NamedQuery(name = "Tipojubilacion.findByNumeroaportes", query = "SELECT t FROM Tipojubilacion t WHERE t.numeroaportes = :numeroaportes")
    , @NamedQuery(name = "Tipojubilacion.findByConsecutivas", query = "SELECT t FROM Tipojubilacion t WHERE t.consecutivas = :consecutivas")
    , @NamedQuery(name = "Tipojubilacion.findByDiscapacidad", query = "SELECT t FROM Tipojubilacion t WHERE t.discapacidad = :discapacidad")})
public class Tipojubilacion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "edadminima")
    private Integer edadminima;
    @Column(name = "numeroaportes")
    private Integer numeroaportes;
    @Column(name = "consecutivas")
    private Integer consecutivas;
    @Column(name = "discapacidad")
    private Boolean discapacidad;

    public Tipojubilacion() {
    }

    public Tipojubilacion(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getEdadminima() {
        return edadminima;
    }

    public void setEdadminima(Integer edadminima) {
        this.edadminima = edadminima;
    }

    public Integer getNumeroaportes() {
        return numeroaportes;
    }

    public void setNumeroaportes(Integer numeroaportes) {
        this.numeroaportes = numeroaportes;
    }

    public Integer getConsecutivas() {
        return consecutivas;
    }

    public void setConsecutivas(Integer consecutivas) {
        this.consecutivas = consecutivas;
    }

    public Boolean getDiscapacidad() {
        return discapacidad;
    }

    public void setDiscapacidad(Boolean discapacidad) {
        this.discapacidad = discapacidad;
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
        if (!(object instanceof Tipojubilacion)) {
            return false;
        }
        Tipojubilacion other = (Tipojubilacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nombre;
    }
    
}
