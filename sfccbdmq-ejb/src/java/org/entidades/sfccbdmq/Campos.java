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
@Table(name = "campos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Campos.findAll", query = "SELECT c FROM Campos c"),
    @NamedQuery(name = "Campos.findById", query = "SELECT c FROM Campos c WHERE c.id = :id"),
    @NamedQuery(name = "Campos.findByNombre", query = "SELECT c FROM Campos c WHERE c.nombre = :nombre"),
    @NamedQuery(name = "Campos.findByCuenta", query = "SELECT c FROM Campos c WHERE c.cuenta = :cuenta"),
    @NamedQuery(name = "Campos.findBySigno", query = "SELECT c FROM Campos c WHERE c.signo = :signo")})
public class Campos implements Serializable {
//    @OneToMany(mappedBy = "nombramiento")
//    private List<Historialcargos> historialcargosList;
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
    @Column(name = "signo")
    private Integer signo;
    @OneToMany(mappedBy = "campo")
    private List<Lineas> lineasList;
    @OneToMany(mappedBy = "campo2")
    private List<Operaciones> operacionesList;
    @OneToMany(mappedBy = "campo1")
    private List<Operaciones> operacionesList1;

    public Campos() {
    }

    public Campos(Integer id) {
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

    public Integer getSigno() {
        return signo;
    }

    public void setSigno(Integer signo) {
        this.signo = signo;
    }

    @XmlTransient
    public List<Lineas> getLineasList() {
        return lineasList;
    }

    public void setLineasList(List<Lineas> lineasList) {
        this.lineasList = lineasList;
    }

    @XmlTransient
    public List<Operaciones> getOperacionesList() {
        return operacionesList;
    }

    public void setOperacionesList(List<Operaciones> operacionesList) {
        this.operacionesList = operacionesList;
    }

    @XmlTransient
    public List<Operaciones> getOperacionesList1() {
        return operacionesList1;
    }

    public void setOperacionesList1(List<Operaciones> operacionesList1) {
        this.operacionesList1 = operacionesList1;
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
        if (!(object instanceof Campos)) {
            return false;
        }
        Campos other = (Campos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nombre +" ["+cuenta+"]";
    }

//    @XmlTransient
//    public List<Historialcargos> getHistorialcargosList() {
//        return historialcargosList;
//    }
//
//    public void setHistorialcargosList(List<Historialcargos> historialcargosList) {
//        this.historialcargosList = historialcargosList;
//    }
    
}
