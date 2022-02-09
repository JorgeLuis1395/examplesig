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
@Table(name = "ubicaciones")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Ubicaciones.findAll", query = "SELECT u FROM Ubicaciones u"),
    @NamedQuery(name = "Ubicaciones.findById", query = "SELECT u FROM Ubicaciones u WHERE u.id = :id"),
    @NamedQuery(name = "Ubicaciones.findByCodigo", query = "SELECT u FROM Ubicaciones u WHERE u.codigo = :codigo"),
    @NamedQuery(name = "Ubicaciones.findByNombre", query = "SELECT u FROM Ubicaciones u WHERE u.nombre = :nombre")})
public class Ubicaciones implements Serializable {
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
    @OneToMany(mappedBy = "ciudad")
    private List<Edificios> edificiosList;
    @OneToMany(mappedBy = "provincia")
    private List<Ubicaciones> ubicacionesList;
    @JoinColumn(name = "provincia", referencedColumnName = "id")
    @ManyToOne
    private Ubicaciones provincia;

    public Ubicaciones() {
    }

    public Ubicaciones(Integer id) {
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
    public List<Edificios> getEdificiosList() {
        return edificiosList;
    }

    public void setEdificiosList(List<Edificios> edificiosList) {
        this.edificiosList = edificiosList;
    }

    @XmlTransient
    public List<Ubicaciones> getUbicacionesList() {
        return ubicacionesList;
    }

    public void setUbicacionesList(List<Ubicaciones> ubicacionesList) {
        this.ubicacionesList = ubicacionesList;
    }

    public Ubicaciones getProvincia() {
        return provincia;
    }

    public void setProvincia(Ubicaciones provincia) {
        this.provincia = provincia;
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
        if (!(object instanceof Ubicaciones)) {
            return false;
        }
        Ubicaciones other = (Ubicaciones) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return codigo +" - "+nombre;
    }
    
}
