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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "bodegas")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Bodegas.findAll", query = "SELECT b FROM Bodegas b")
    ,
    @NamedQuery(name = "Bodegas.findById", query = "SELECT b FROM Bodegas b WHERE b.id = :id")
    ,
    @NamedQuery(name = "Bodegas.findByNombre", query = "SELECT b FROM Bodegas b WHERE b.nombre = :nombre")
    ,
    @NamedQuery(name = "Bodegas.findByVenta", query = "SELECT b FROM Bodegas b WHERE b.venta = :venta")})
public class Bodegas implements Serializable {
    @OneToMany(mappedBy = "bodega")
    private List<Permisosbodegas> permisosbodegasList;
    @OneToMany(mappedBy = "bodega")
    private List<Tomafisicasuministros> tomafisicasuministrosList;
    @OneToMany(mappedBy = "bodega")
    private List<Cabecerainventario> cabecerainventarioList;
    @OneToMany(mappedBy = "bodega")
    private List<Bodegasuministro> bodegasuministroList;
    @OneToMany(mappedBy = "bodega")
    private List<Kardexinventario> kardexinventarioList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "venta")
    private Boolean venta;
    @JoinColumn(name = "sucursal", referencedColumnName = "id")
    @ManyToOne
    private Sucursales sucursal;
    @Transient
    private float saldo;
    @Transient
    private float saldoinv;

    public Bodegas() {
    }

    public Bodegas(Integer id) {
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

    public Boolean getVenta() {
        return venta;
    }

    public void setVenta(Boolean venta) {
        this.venta = venta;
    }

    public Sucursales getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursales sucursal) {
        this.sucursal = sucursal;
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
        if (!(object instanceof Bodegas)) {
            return false;
        }
        Bodegas other = (Bodegas) object;
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
    public List<Cabecerainventario> getCabecerainventarioList() {
        return cabecerainventarioList;
    }

    public void setCabecerainventarioList(List<Cabecerainventario> cabecerainventarioList) {
        this.cabecerainventarioList = cabecerainventarioList;
    }

    @XmlTransient
    public List<Bodegasuministro> getBodegasuministroList() {
        return bodegasuministroList;
    }

    public void setBodegasuministroList(List<Bodegasuministro> bodegasuministroList) {
        this.bodegasuministroList = bodegasuministroList;
    }

    @XmlTransient
    public List<Kardexinventario> getKardexinventarioList() {
        return kardexinventarioList;
    }

    public void setKardexinventarioList(List<Kardexinventario> kardexinventarioList) {
        this.kardexinventarioList = kardexinventarioList;
    }

    @XmlTransient
    public List<Tomafisicasuministros> getTomafisicasuministrosList() {
        return tomafisicasuministrosList;
    }

    public void setTomafisicasuministrosList(List<Tomafisicasuministros> tomafisicasuministrosList) {
        this.tomafisicasuministrosList = tomafisicasuministrosList;
    }

    /**
     * @return the saldo
     */
    public float getSaldo() {
        return saldo;
    }

    /**
     * @param saldo the saldo to set
     */
    public void setSaldo(float saldo) {
        this.saldo = saldo;
    }

   

    @XmlTransient
    @JsonIgnore
    public List<Permisosbodegas> getPermisosbodegasList() {
        return permisosbodegasList;
    }

    public void setPermisosbodegasList(List<Permisosbodegas> permisosbodegasList) {
        this.permisosbodegasList = permisosbodegasList;
    }

    /**
     * @return the saldoinv
     */
    public float getSaldoinv() {
        return saldoinv;
    }

    /**
     * @param saldoinv the saldoinv to set
     */
    public void setSaldoinv(float saldoinv) {
        this.saldoinv = saldoinv;
    }


}