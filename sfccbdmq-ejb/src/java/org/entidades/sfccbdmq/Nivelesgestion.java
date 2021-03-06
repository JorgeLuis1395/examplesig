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
@Table(name = "nivelesgestion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Nivelesgestion.findAll", query = "SELECT n FROM Nivelesgestion n"),
    @NamedQuery(name = "Nivelesgestion.findById", query = "SELECT n FROM Nivelesgestion n WHERE n.id = :id"),
    @NamedQuery(name = "Nivelesgestion.findByCodigo", query = "SELECT n FROM Nivelesgestion n WHERE n.codigo = :codigo"),
    @NamedQuery(name = "Nivelesgestion.findByNombre", query = "SELECT n FROM Nivelesgestion n WHERE n.nombre = :nombre"),
    @NamedQuery(name = "Nivelesgestion.findByActivo", query = "SELECT n FROM Nivelesgestion n WHERE n.activo = :activo"),
    @NamedQuery(name = "Nivelesgestion.findByDescripcion", query = "SELECT n FROM Nivelesgestion n WHERE n.descripcion = :descripcion")})
public class Nivelesgestion implements Serializable {
    @OneToMany(mappedBy = "niveldegestion")
    private List<Escalassalariales> escalassalarialesList;
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
    @Column(name = "activo")
    private Boolean activo;
    @Size(max = 2147483647)
    @Column(name = "descripcion")
    private String descripcion;
    @OneToMany(mappedBy = "nivelgestion")
    private List<Formularios> formulariosList;
    @OneToMany(mappedBy = "nivel")
    private List<Cargos> cargosList;
    @OneToMany(mappedBy = "nivelgestion")
    private List<Pondnivelgestion> pondnivelgestionList;

    public Nivelesgestion() {
    }

    public Nivelesgestion(Integer id) {
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

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @XmlTransient
    public List<Formularios> getFormulariosList() {
        return formulariosList;
    }

    public void setFormulariosList(List<Formularios> formulariosList) {
        this.formulariosList = formulariosList;
    }

    @XmlTransient
    public List<Cargos> getCargosList() {
        return cargosList;
    }

    public void setCargosList(List<Cargos> cargosList) {
        this.cargosList = cargosList;
    }

    @XmlTransient
    public List<Pondnivelgestion> getPondnivelgestionList() {
        return pondnivelgestionList;
    }

    public void setPondnivelgestionList(List<Pondnivelgestion> pondnivelgestionList) {
        this.pondnivelgestionList = pondnivelgestionList;
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
        if (!(object instanceof Nivelesgestion)) {
            return false;
        }
        Nivelesgestion other = (Nivelesgestion) object;
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
    public List<Escalassalariales> getEscalassalarialesList() {
        return escalassalarialesList;
    }

    public void setEscalassalarialesList(List<Escalassalariales> escalassalarialesList) {
        this.escalassalarialesList = escalassalarialesList;
    }
    
}
