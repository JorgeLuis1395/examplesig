/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "formularios")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Formularios.findAll", query = "SELECT f FROM Formularios f"),
    @NamedQuery(name = "Formularios.findById", query = "SELECT f FROM Formularios f WHERE f.id = :id"),
    @NamedQuery(name = "Formularios.findByActivo", query = "SELECT f FROM Formularios f WHERE f.activo = :activo"),
    @NamedQuery(name = "Formularios.findByNombre", query = "SELECT f FROM Formularios f WHERE f.nombre = :nombre"),
    @NamedQuery(name = "Formularios.findByFecha", query = "SELECT f FROM Formularios f WHERE f.fecha = :fecha")})
public class Formularios implements Serializable {
    @OneToMany(mappedBy = "formulario")
    private List<Configuracionesev> configuracionesevList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "activo")
    private Boolean activo;
    @Size(max = 2147483647)
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @OneToMany(mappedBy = "formulario")
    private List<Preguntas> preguntasList;
    @JoinColumn(name = "nivelgestion", referencedColumnName = "id")
    @ManyToOne
    private Nivelesgestion nivelgestion;

    public Formularios() {
    }

    public Formularios(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    @XmlTransient
    public List<Preguntas> getPreguntasList() {
        return preguntasList;
    }

    public void setPreguntasList(List<Preguntas> preguntasList) {
        this.preguntasList = preguntasList;
    }

    public Nivelesgestion getNivelgestion() {
        return nivelgestion;
    }

    public void setNivelgestion(Nivelesgestion nivelgestion) {
        this.nivelgestion = nivelgestion;
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
        if (!(object instanceof Formularios)) {
            return false;
        }
        Formularios other = (Formularios) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Formularios[ id=" + id + " ]";
    }

    @XmlTransient
    public List<Configuracionesev> getConfiguracionesevList() {
        return configuracionesevList;
    }

    public void setConfiguracionesevList(List<Configuracionesev> configuracionesevList) {
        this.configuracionesevList = configuracionesevList;
    }
    
}
