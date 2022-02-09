/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "tipoegreso")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tipoegreso.findAll", query = "SELECT t FROM Tipoegreso t"),
    @NamedQuery(name = "Tipoegreso.findById", query = "SELECT t FROM Tipoegreso t WHERE t.id = :id"),
    @NamedQuery(name = "Tipoegreso.findByNombre", query = "SELECT t FROM Tipoegreso t WHERE t.nombre = :nombre"),
    @NamedQuery(name = "Tipoegreso.findByCuenta", query = "SELECT t FROM Tipoegreso t WHERE t.cuenta = :cuenta"),
    @NamedQuery(name = "Tipoegreso.findByCreditos", query = "SELECT t FROM Tipoegreso t WHERE t.creditos = :creditos"),
    @NamedQuery(name = "Tipoegreso.findByEgreso", query = "SELECT t FROM Tipoegreso t WHERE t.egreso = :egreso")})
public class Tipoegreso implements Serializable {
    @OneToMany(mappedBy = "tipoegreso")
    private List<Rascompras> rascomprasList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "nombre")
    private String nombre;
    @Size(max = 2147483647)
    @Column(name = "cuenta")
    private String cuenta;
    @Column(name = "creditos")
    private Boolean creditos;
    @Column(name = "egreso")
    private Boolean egreso;

    public Tipoegreso() {
    }

    public Tipoegreso(Integer id) {
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

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public Boolean getCreditos() {
        return creditos;
    }

    public void setCreditos(Boolean creditos) {
        this.creditos = creditos;
    }

    public Boolean getEgreso() {
        return egreso;
    }

    public void setEgreso(Boolean egreso) {
        this.egreso = egreso;
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
        if (!(object instanceof Tipoegreso)) {
            return false;
        }
        Tipoegreso other = (Tipoegreso) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nombre;
    }

    @XmlTransient
    public List<Rascompras> getRascomprasList() {
        return rascomprasList;
    }

    public void setRascomprasList(List<Rascompras> rascomprasList) {
        this.rascomprasList = rascomprasList;
    }
    
}
