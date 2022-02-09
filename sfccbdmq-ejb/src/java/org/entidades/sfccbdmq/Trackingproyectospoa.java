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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author luis
 */
@Entity
@Table(name = "trackingproyectospoa")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Trackingproyectospoa.findAll", query = "SELECT t FROM Trackingproyectospoa t")
    , @NamedQuery(name = "Trackingproyectospoa.findById", query = "SELECT t FROM Trackingproyectospoa t WHERE t.id = :id")
    , @NamedQuery(name = "Trackingproyectospoa.findByFechainicio", query = "SELECT t FROM Trackingproyectospoa t WHERE t.fechainicio = :fechainicio")
    , @NamedQuery(name = "Trackingproyectospoa.findByFechapublicacion", query = "SELECT t FROM Trackingproyectospoa t WHERE t.fechapublicacion = :fechapublicacion")
    , @NamedQuery(name = "Trackingproyectospoa.findByFechadesierto", query = "SELECT t FROM Trackingproyectospoa t WHERE t.fechadesierto = :fechadesierto")
    , @NamedQuery(name = "Trackingproyectospoa.findByUsuario", query = "SELECT t FROM Trackingproyectospoa t WHERE t.usuario = :usuario")
    , @NamedQuery(name = "Trackingproyectospoa.findByObservaciones", query = "SELECT t FROM Trackingproyectospoa t WHERE t.observaciones = :observaciones")})
public class Trackingproyectospoa implements Serializable {

    @Size(max = 2147483647)
    @Column(name = "analista")
    private String analista;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "fechainicio")
    @Temporal(TemporalType.DATE)
    private Date fechainicio;
    @Column(name = "fechapublicacion")
    @Temporal(TemporalType.DATE)
    private Date fechapublicacion;
    @Column(name = "fechadesierto")
    @Temporal(TemporalType.DATE)
    private Date fechadesierto;
    @Size(max = 2147483647)
    @Column(name = "usuario")
    private String usuario;
    @Size(max = 2147483647)
    @Column(name = "observaciones")
    private String observaciones;
    @JoinColumn(name = "proyecto", referencedColumnName = "id")
    @ManyToOne
    private Proyectospoa proyecto;

    public Trackingproyectospoa() {
    }

    public Trackingproyectospoa(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFechainicio() {
        return fechainicio;
    }

    public void setFechainicio(Date fechainicio) {
        this.fechainicio = fechainicio;
    }

    public Date getFechapublicacion() {
        return fechapublicacion;
    }

    public void setFechapublicacion(Date fechapublicacion) {
        this.fechapublicacion = fechapublicacion;
    }

    public Date getFechadesierto() {
        return fechadesierto;
    }

    public void setFechadesierto(Date fechadesierto) {
        this.fechadesierto = fechadesierto;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
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
        if (!(object instanceof Trackingproyectospoa)) {
            return false;
        }
        Trackingproyectospoa other = (Trackingproyectospoa) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cbdmqpacpoa.entidades.Trackingproyectospoa[ id=" + id + " ]";
    }

    public String getAnalista() {
        return analista;
    }

    public void setAnalista(String analista) {
        this.analista = analista;
    }
    
}
