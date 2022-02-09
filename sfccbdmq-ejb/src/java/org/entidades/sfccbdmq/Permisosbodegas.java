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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "permisosbodegas")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Permisosbodegas.findAll", query = "SELECT p FROM Permisosbodegas p")
    , @NamedQuery(name = "Permisosbodegas.findById", query = "SELECT p FROM Permisosbodegas p WHERE p.id = :id")
    , @NamedQuery(name = "Permisosbodegas.findByIngreso", query = "SELECT p FROM Permisosbodegas p WHERE p.ingreso = :ingreso")
    , @NamedQuery(name = "Permisosbodegas.findByRecepcion", query = "SELECT p FROM Permisosbodegas p WHERE p.recepcion = :recepcion")
    , @NamedQuery(name = "Permisosbodegas.findByRevision", query = "SELECT p FROM Permisosbodegas p WHERE p.revision = :revision")
    , @NamedQuery(name = "Permisosbodegas.findByAprobacion", query = "SELECT p FROM Permisosbodegas p WHERE p.aprobacion = :aprobacion")
    , @NamedQuery(name = "Permisosbodegas.findByRegistro", query = "SELECT p FROM Permisosbodegas p WHERE p.registro = :registro")
    , @NamedQuery(name = "Permisosbodegas.findByDespacho", query = "SELECT p FROM Permisosbodegas p WHERE p.despacho = :despacho")
    , @NamedQuery(name = "Permisosbodegas.findByIngresot", query = "SELECT p FROM Permisosbodegas p WHERE p.ingresot = :ingresot")
    , @NamedQuery(name = "Permisosbodegas.findByAprobacionIngreso", query = "SELECT p FROM Permisosbodegas p WHERE p.aprobacionIngreso = :aprobacionIngreso")
    , @NamedQuery(name = "Permisosbodegas.findByAprobacionRecepcion", query = "SELECT p FROM Permisosbodegas p WHERE p.aprobacionRecepcion = :aprobacionRecepcion")})
public class Permisosbodegas implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ingreso")
    private Boolean ingreso;
    @Column(name = "recepcion")
    private Boolean recepcion;
    @Column(name = "revision")
    private Boolean revision;
    @Column(name = "aprobacion")
    private Boolean aprobacion;
    @Column(name = "registro")
    private Boolean registro;
    @Column(name = "despacho")
    private Boolean despacho;
    @Column(name = "ingresot")
    private Boolean ingresot;
    @Column(name = "aprobacion_ingreso")
    private Boolean aprobacionIngreso;
    @Column(name = "aprobacion_recepcion")
    private Boolean aprobacionRecepcion;
    @JoinColumn(name = "bodega", referencedColumnName = "id")
    @ManyToOne
    private Bodegas bodega;
    @JoinColumn(name = "usuario", referencedColumnName = "id")
    @ManyToOne
    private Empleados usuario;

    public Permisosbodegas() {
    }

    public Permisosbodegas(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getIngreso() {
        return ingreso;
    }

    public void setIngreso(Boolean ingreso) {
        this.ingreso = ingreso;
    }

    public Boolean getRecepcion() {
        return recepcion;
    }

    public void setRecepcion(Boolean recepcion) {
        this.recepcion = recepcion;
    }

    public Boolean getRevision() {
        return revision;
    }

    public void setRevision(Boolean revision) {
        this.revision = revision;
    }

    public Boolean getAprobacion() {
        return aprobacion;
    }

    public void setAprobacion(Boolean aprobacion) {
        this.aprobacion = aprobacion;
    }

    public Boolean getRegistro() {
        return registro;
    }

    public void setRegistro(Boolean registro) {
        this.registro = registro;
    }

    public Boolean getDespacho() {
        return despacho;
    }

    public void setDespacho(Boolean despacho) {
        this.despacho = despacho;
    }

    public Boolean getIngresot() {
        return ingresot;
    }

    public void setIngresot(Boolean ingresot) {
        this.ingresot = ingresot;
    }

    public Boolean getAprobacionIngreso() {
        return aprobacionIngreso;
    }

    public void setAprobacionIngreso(Boolean aprobacionIngreso) {
        this.aprobacionIngreso = aprobacionIngreso;
    }

    public Boolean getAprobacionRecepcion() {
        return aprobacionRecepcion;
    }

    public void setAprobacionRecepcion(Boolean aprobacionRecepcion) {
        this.aprobacionRecepcion = aprobacionRecepcion;
    }

    public Bodegas getBodega() {
        return bodega;
    }

    public void setBodega(Bodegas bodega) {
        this.bodega = bodega;
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
        if (!(object instanceof Permisosbodegas)) {
            return false;
        }
        Permisosbodegas other = (Permisosbodegas) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.sfccbdmq.Permisosbodegas[ id=" + id + " ]";
    }
    
}