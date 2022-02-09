/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import org.entidades.sfccbdmq.Cabecerainventario;
import org.entidades.sfccbdmq.Empleados;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "bitacorasuministro")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Bitacorasuministro.findAll", query = "SELECT b FROM Bitacorasuministro b")
    , @NamedQuery(name = "Bitacorasuministro.findById", query = "SELECT b FROM Bitacorasuministro b WHERE b.id = :id")
    , @NamedQuery(name = "Bitacorasuministro.findByFecha", query = "SELECT b FROM Bitacorasuministro b WHERE b.fecha = :fecha")
    , @NamedQuery(name = "Bitacorasuministro.findByEstado", query = "SELECT b FROM Bitacorasuministro b WHERE b.estado = :estado")})
public class Bitacorasuministro implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Column(name = "estado")
    private Integer estado;
    @JoinColumn(name = "cabecera", referencedColumnName = "id")
    @ManyToOne
    private Cabecerainventario cabecera;
    @JoinColumn(name = "usuario", referencedColumnName = "id")
    @ManyToOne
    private Empleados usuario;

    public Bitacorasuministro() {
    }

    public Bitacorasuministro(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Cabecerainventario getCabecera() {
        return cabecera;
    }

    public void setCabecera(Cabecerainventario cabecera) {
        this.cabecera = cabecera;
    }

    public Empleados getUsuario() {
        return usuario;
    }

    public void setUsuario(Empleados usuario) {
        this.usuario = usuario;
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
        if (!(object instanceof Bitacorasuministro)) {
            return false;
        }
        Bitacorasuministro other = (Bitacorasuministro) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.beans.sfccbdmq.Bitacorasuministro[ id=" + id + " ]";
    }
    
}