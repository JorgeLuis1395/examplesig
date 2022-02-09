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
 * @author luis
 */
@Entity
@Table(name = "partidaspoa")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Partidaspoa.findAll", query = "SELECT p FROM Partidaspoa p")
    , @NamedQuery(name = "Partidaspoa.findById", query = "SELECT p FROM Partidaspoa p WHERE p.id = :id")
    , @NamedQuery(name = "Partidaspoa.findByCodigo", query = "SELECT p FROM Partidaspoa p WHERE p.codigo = :codigo")
    , @NamedQuery(name = "Partidaspoa.findByNombre", query = "SELECT p FROM Partidaspoa p WHERE p.nombre = :nombre")
    , @NamedQuery(name = "Partidaspoa.findByIngreso", query = "SELECT p FROM Partidaspoa p WHERE p.ingreso = :ingreso")
    , @NamedQuery(name = "Partidaspoa.findByActivo", query = "SELECT p FROM Partidaspoa p WHERE p.activo = :activo")
    , @NamedQuery(name = "Partidaspoa.findByImputable", query = "SELECT p FROM Partidaspoa p WHERE p.imputable = :imputable")})
public class Partidaspoa implements Serializable {

    @OneToMany(mappedBy = "partida")
    private List<Ejecucionpoa> ejecucionpoaList;

    @OneToMany(mappedBy = "partida")
    private List<Flujodecajapoa> flujodecajapoaList;

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
    @Column(name = "activo")
    private Boolean activo;
    @Column(name = "imputable")
    private Boolean imputable;
    @OneToMany(mappedBy = "partida")
    private List<Asignacionespoa> asignacionespoaList;

    public Partidaspoa() {
    }

    public Partidaspoa(Integer id) {
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

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Boolean getImputable() {
        return imputable;
    }

    public void setImputable(Boolean imputable) {
        this.imputable = imputable;
    }

    @XmlTransient
    public List<Asignacionespoa> getAsignacionespoaList() {
        return asignacionespoaList;
    }

    public void setAsignacionespoaList(List<Asignacionespoa> asignacionespoaList) {
        this.asignacionespoaList = asignacionespoaList;
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
        if (!(object instanceof Partidaspoa)) {
            return false;
        }
        Partidaspoa other = (Partidaspoa) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre;
    }

    @XmlTransient
    public List<Flujodecajapoa> getFlujodecajapoaList() {
        return flujodecajapoaList;
    }

    public void setFlujodecajapoaList(List<Flujodecajapoa> flujodecajapoaList) {
        this.flujodecajapoaList = flujodecajapoaList;
    }

    @XmlTransient
    public List<Ejecucionpoa> getEjecucionpoaList() {
        return ejecucionpoaList;
    }

    public void setEjecucionpoaList(List<Ejecucionpoa> ejecucionpoaList) {
        this.ejecucionpoaList = ejecucionpoaList;
    }
    
}
