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
@Table(name = "clasificadores")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Clasificadores.findAll", query = "SELECT c FROM Clasificadores c"),
    @NamedQuery(name = "Clasificadores.findById", query = "SELECT c FROM Clasificadores c WHERE c.id = :id"),
    @NamedQuery(name = "Clasificadores.findByCodigo", query = "SELECT c FROM Clasificadores c WHERE c.codigo = :codigo"),
    @NamedQuery(name = "Clasificadores.findByNombre", query = "SELECT c FROM Clasificadores c WHERE c.nombre = :nombre"),
    @NamedQuery(name = "Clasificadores.findByIngreso", query = "SELECT c FROM Clasificadores c WHERE c.ingreso = :ingreso"),
    @NamedQuery(name = "Clasificadores.findByImputable", query = "SELECT c FROM Clasificadores c WHERE c.imputable = :imputable")})
public class Clasificadores implements Serializable {
    @OneToMany(mappedBy = "partida")
    private List<Flujodecaja> flujodecajaList;
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
    @Column(name = "ingreso")
    private Boolean ingreso;
    @Column(name = "imputable")
    private Boolean imputable;
    @Column(name = "activo")
    private Boolean activo;
    @OneToMany(mappedBy = "clasificador")
    private List<Asignaciones> asignacionesList;

    public Clasificadores() {
    }

    public Clasificadores(Integer id) {
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

    public Boolean getIngreso() {
        return ingreso;
    }

    public void setIngreso(Boolean ingreso) {
        this.ingreso = ingreso;
    }

    public Boolean getImputable() {
        return imputable;
    }

    public void setImputable(Boolean imputable) {
        this.imputable = imputable;
    }

    @XmlTransient
    public List<Asignaciones> getAsignacionesList() {
        return asignacionesList;
    }

    public void setAsignacionesList(List<Asignaciones> asignacionesList) {
        this.asignacionesList = asignacionesList;
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
        if (!(object instanceof Clasificadores)) {
            return false;
        }
        Clasificadores other = (Clasificadores) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return codigo+" - "+nombre;
    }

    /**
     * @return the activo
     */
    public Boolean getActivo() {
        return activo;
    }

    /**
     * @param activo the activo to set
     */
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    @XmlTransient
    public List<Flujodecaja> getFlujodecajaList() {
        return flujodecajaList;
    }

    public void setFlujodecajaList(List<Flujodecaja> flujodecajaList) {
        this.flujodecajaList = flujodecajaList;
    }
    
}
