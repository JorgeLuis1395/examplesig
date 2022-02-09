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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "tipoactivo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tipoactivo.findAll", query = "SELECT t FROM Tipoactivo t"),
    @NamedQuery(name = "Tipoactivo.findById", query = "SELECT t FROM Tipoactivo t WHERE t.id = :id"),
    @NamedQuery(name = "Tipoactivo.findByCodigo", query = "SELECT t FROM Tipoactivo t WHERE t.codigo = :codigo"),
    @NamedQuery(name = "Tipoactivo.findByNombre", query = "SELECT t FROM Tipoactivo t WHERE t.nombre = :nombre"),
    @NamedQuery(name = "Tipoactivo.findByImputable", query = "SELECT t FROM Tipoactivo t WHERE t.imputable = :imputable")})
public class Tipoactivo implements Serializable {
    @Size(max = 2147483647)
    @Column(name = "cuenta")
    private String cuenta;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "codigo")
    private String codigo;
    @Size(max = 2147483647)
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "imputable")
    private Boolean imputable;
    @OneToMany(mappedBy = "tipo")
    private List<Grupoactivos> grupoactivosList;
    @OneToMany(mappedBy = "padre")
    private List<Tipoactivo> tipoactivoList;
    @JoinColumn(name = "padre", referencedColumnName = "id")
    @ManyToOne
    private Tipoactivo padre;

    public Tipoactivo() {
    }

    public Tipoactivo(Integer id) {
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Boolean getImputable() {
        return imputable;
    }

    public void setImputable(Boolean imputable) {
        this.imputable = imputable;
    }

    @XmlTransient
    public List<Grupoactivos> getGrupoactivosList() {
        return grupoactivosList;
    }

    public void setGrupoactivosList(List<Grupoactivos> grupoactivosList) {
        this.grupoactivosList = grupoactivosList;
    }

    @XmlTransient
    public List<Tipoactivo> getTipoactivoList() {
        return tipoactivoList;
    }

    public void setTipoactivoList(List<Tipoactivo> tipoactivoList) {
        this.tipoactivoList = tipoactivoList;
    }

    public Tipoactivo getPadre() {
        return padre;
    }

    public void setPadre(Tipoactivo padre) {
        this.padre = padre;
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
        if (!(object instanceof Tipoactivo)) {
            return false;
        }
        Tipoactivo other = (Tipoactivo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return codigo+" - "+nombre;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }
    
}
