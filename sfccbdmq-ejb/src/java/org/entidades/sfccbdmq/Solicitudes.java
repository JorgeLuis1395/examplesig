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
 * @author luis
 */
@Entity
@Table(name = "solicitudes")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Solicitudes.findAll", query = "SELECT s FROM Solicitudes s")
    , @NamedQuery(name = "Solicitudes.findById", query = "SELECT s FROM Solicitudes s WHERE s.id = :id")
    , @NamedQuery(name = "Solicitudes.findByFechasolicitud", query = "SELECT s FROM Solicitudes s WHERE s.fechasolicitud = :fechasolicitud")
    , @NamedQuery(name = "Solicitudes.findByUsuariosolicitante", query = "SELECT s FROM Solicitudes s WHERE s.usuariosolicitante = :usuariosolicitante")
    , @NamedQuery(name = "Solicitudes.findByUsuariotranferir", query = "SELECT s FROM Solicitudes s WHERE s.usuariotranferir = :usuariotranferir")
    , @NamedQuery(name = "Solicitudes.findByFecharespuesta", query = "SELECT s FROM Solicitudes s WHERE s.fecharespuesta = :fecharespuesta")
    , @NamedQuery(name = "Solicitudes.findByEstado", query = "SELECT s FROM Solicitudes s WHERE s.estado = :estado")
    , @NamedQuery(name = "Solicitudes.findByUsuarioaprobacion", query = "SELECT s FROM Solicitudes s WHERE s.usuarioaprobacion = :usuarioaprobacion")})
public class Solicitudes implements Serializable {

    @Column(name = "bodega")
    private Boolean bodega;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "fechasolicitud")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechasolicitud;
    @Size(max = 2147483647)
    @Column(name = "usuariosolicitante")
    private String usuariosolicitante;
    @Size(max = 2147483647)
    @Column(name = "usuariotranferir")
    private String usuariotranferir;
    @Column(name = "fecharespuesta")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecharespuesta;
    @Column(name = "estado")
    private Integer estado;
    @Size(max = 2147483647)
    @Column(name = "usuarioaprobacion")
    private String usuarioaprobacion;
    @OneToMany(mappedBy = "solicitud")
    private List<Transferencias> transferenciasList;

    public Solicitudes() {
    }

    public Solicitudes(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFechasolicitud() {
        return fechasolicitud;
    }

    public void setFechasolicitud(Date fechasolicitud) {
        this.fechasolicitud = fechasolicitud;
    }

    public String getUsuariosolicitante() {
        return usuariosolicitante;
    }

    public void setUsuariosolicitante(String usuariosolicitante) {
        this.usuariosolicitante = usuariosolicitante;
    }

    public String getUsuariotranferir() {
        return usuariotranferir;
    }

    public void setUsuariotranferir(String usuariotranferir) {
        this.usuariotranferir = usuariotranferir;
    }

    public Date getFecharespuesta() {
        return fecharespuesta;
    }

    public void setFecharespuesta(Date fecharespuesta) {
        this.fecharespuesta = fecharespuesta;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public String getUsuarioaprobacion() {
        return usuarioaprobacion;
    }

    public void setUsuarioaprobacion(String usuarioaprobacion) {
        this.usuarioaprobacion = usuarioaprobacion;
    }

    @XmlTransient
    public List<Transferencias> getTransferenciasList() {
        return transferenciasList;
    }

    public void setTransferenciasList(List<Transferencias> transferenciasList) {
        this.transferenciasList = transferenciasList;
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
        if (!(object instanceof Solicitudes)) {
            return false;
        }
        Solicitudes other = (Solicitudes) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.sfccbdmq.Solicitudes[ id=" + id + " ]";
    }

    public Boolean getBodega() {
        return bodega;
    }

    public void setBodega(Boolean bodega) {
        this.bodega = bodega;
    }
    
}