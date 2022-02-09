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
@Table(name = "subgruposactivos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Subgruposactivos.findAll", query = "SELECT s FROM Subgruposactivos s"),
    @NamedQuery(name = "Subgruposactivos.findById", query = "SELECT s FROM Subgruposactivos s WHERE s.id = :id"),
    @NamedQuery(name = "Subgruposactivos.findByCodigo", query = "SELECT s FROM Subgruposactivos s WHERE s.codigo = :codigo"),
    @NamedQuery(name = "Subgruposactivos.findByNombre", query = "SELECT s FROM Subgruposactivos s WHERE s.nombre = :nombre")})
public class Subgruposactivos implements Serializable {
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
    @OneToMany(mappedBy = "subgrupo")
    private List<Activos> activosList;
    @JoinColumn(name = "grupo", referencedColumnName = "id")
    @ManyToOne
    private Grupoactivos grupo;

    public Subgruposactivos() {
    }

    public Subgruposactivos(Integer id) {
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

    @XmlTransient
    public List<Activos> getActivosList() {
        return activosList;
    }

    public void setActivosList(List<Activos> activosList) {
        this.activosList = activosList;
    }

    public Grupoactivos getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupoactivos grupo) {
        this.grupo = grupo;
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
        if (!(object instanceof Subgruposactivos)) {
            return false;
        }
        Subgruposactivos other = (Subgruposactivos) object;
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
