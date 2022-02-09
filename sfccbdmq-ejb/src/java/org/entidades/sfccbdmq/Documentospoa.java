/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
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
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author luis
 */
@Entity
@Table(name = "documentospoa")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Documentospoa.findAll", query = "SELECT d FROM Documentospoa d")
    , @NamedQuery(name = "Documentospoa.findById", query = "SELECT d FROM Documentospoa d WHERE d.id = :id")
    , @NamedQuery(name = "Documentospoa.findByNombrearchivo", query = "SELECT d FROM Documentospoa d WHERE d.nombrearchivo = :nombrearchivo")
    , @NamedQuery(name = "Documentospoa.findByTipo", query = "SELECT d FROM Documentospoa d WHERE d.tipo = :tipo")
    , @NamedQuery(name = "Documentospoa.findByPath", query = "SELECT d FROM Documentospoa d WHERE d.path = :path")})
public class Documentospoa implements Serializable {

    @JoinColumn(name = "cabecerareforma", referencedColumnName = "id")
    @ManyToOne
    private Cabecerareformaspoa cabecerareforma;

    @JoinColumn(name = "certificacion", referencedColumnName = "id")
    @ManyToOne
    private Certificacionespoa certificacion;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "nombrearchivo")
    private String nombrearchivo;
    @Size(max = 2147483647)
    @Column(name = "tipo")
    private String tipo;
    @Size(max = 2147483647)
    @Column(name = "path")
    private String path;
    @JoinColumn(name = "proyecto", referencedColumnName = "id")
    @ManyToOne
    private Proyectospoa proyecto;

    public Documentospoa() {
    }

    public Documentospoa(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombrearchivo() {
        return nombrearchivo;
    }

    public void setNombrearchivo(String nombrearchivo) {
        this.nombrearchivo = nombrearchivo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Proyectospoa getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyectospoa proyecto) {
        this.proyecto = proyecto;
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
        if (!(object instanceof Documentospoa)) {
            return false;
        }
        Documentospoa other = (Documentospoa) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.pacpoaiepi.Documentospoa[ id=" + id + " ]";
    }

    public Certificacionespoa getCertificacion() {
        return certificacion;
    }

    public void setCertificacion(Certificacionespoa certificacion) {
        this.certificacion = certificacion;
    }

    public Cabecerareformaspoa getCabecerareforma() {
        return cabecerareforma;
    }

    public void setCabecerareforma(Cabecerareformaspoa cabecerareforma) {
        this.cabecerareforma = cabecerareforma;
    }
    
}
