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
 * @author edwin
 */
@Entity
@Table(name = "detallesentrevista")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Detallesentrevista.findAll", query = "SELECT d FROM Detallesentrevista d"),
    @NamedQuery(name = "Detallesentrevista.findById", query = "SELECT d FROM Detallesentrevista d WHERE d.id = :id"),
    @NamedQuery(name = "Detallesentrevista.findByDescripcion", query = "SELECT d FROM Detallesentrevista d WHERE d.descripcion = :descripcion"),
    @NamedQuery(name = "Detallesentrevista.findByActivo", query = "SELECT d FROM Detallesentrevista d WHERE d.activo = :activo")})
public class Detallesentrevista implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "activo")
    private Boolean activo;
    @JoinColumn(name = "requerimiento", referencedColumnName = "id")
    @ManyToOne
    private Requerimientoscargo requerimiento;
    @JoinColumn(name = "entrevista", referencedColumnName = "id")
    @ManyToOne
    private Entrevistas entrevista;

    public Detallesentrevista() {
    }

    public Detallesentrevista(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Requerimientoscargo getRequerimiento() {
        return requerimiento;
    }

    public void setRequerimiento(Requerimientoscargo requerimiento) {
        this.requerimiento = requerimiento;
    }

    public Entrevistas getEntrevista() {
        return entrevista;
    }

    public void setEntrevista(Entrevistas entrevista) {
        this.entrevista = entrevista;
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
        if (!(object instanceof Detallesentrevista)) {
            return false;
        }
        Detallesentrevista other = (Detallesentrevista) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Detallesentrevista[ id=" + id + " ]";
    }
    
}
