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
 * @author edwin
 */
@Entity
@Table(name = "verificacionesreferencias")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Verificacionesreferencias.findAll", query = "SELECT v FROM Verificacionesreferencias v"),
    @NamedQuery(name = "Verificacionesreferencias.findById", query = "SELECT v FROM Verificacionesreferencias v WHERE v.id = :id"),
    @NamedQuery(name = "Verificacionesreferencias.findByCausadesvinculacion", query = "SELECT v FROM Verificacionesreferencias v WHERE v.causadesvinculacion = :causadesvinculacion"),
    @NamedQuery(name = "Verificacionesreferencias.findByRecomiendacargo", query = "SELECT v FROM Verificacionesreferencias v WHERE v.recomiendacargo = :recomiendacargo"),
    @NamedQuery(name = "Verificacionesreferencias.findByPuntosdesarrollar", query = "SELECT v FROM Verificacionesreferencias v WHERE v.puntosdesarrollar = :puntosdesarrollar"),
    @NamedQuery(name = "Verificacionesreferencias.findByDescripcioncomportamiento", query = "SELECT v FROM Verificacionesreferencias v WHERE v.descripcioncomportamiento = :descripcioncomportamiento"),
    @NamedQuery(name = "Verificacionesreferencias.findByObservacion", query = "SELECT v FROM Verificacionesreferencias v WHERE v.observacion = :observacion"),
    @NamedQuery(name = "Verificacionesreferencias.findByActivo", query = "SELECT v FROM Verificacionesreferencias v WHERE v.activo = :activo"),
    @NamedQuery(name = "Verificacionesreferencias.findByFecha", query = "SELECT v FROM Verificacionesreferencias v WHERE v.fecha = :fecha")})
public class Verificacionesreferencias implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "causadesvinculacion")
    private String causadesvinculacion;
    @Size(max = 2147483647)
    @Column(name = "recomiendacargo")
    private String recomiendacargo;
    @Size(max = 2147483647)
    @Column(name = "puntosdesarrollar")
    private String puntosdesarrollar;
    @Size(max = 2147483647)
    @Column(name = "descripcioncomportamiento")
    private String descripcioncomportamiento;
    @Size(max = 2147483647)
    @Column(name = "observacion")
    private String observacion;
    @Column(name = "activo")
    private Boolean activo;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @JoinColumn(name = "recomendacion", referencedColumnName = "id")
    @ManyToOne
    private Recomendaciones recomendacion;
    @JoinColumn(name = "experiencia", referencedColumnName = "id")
    @ManyToOne
    private Experiencias experiencia;

    public Verificacionesreferencias() {
    }

    public Verificacionesreferencias(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCausadesvinculacion() {
        return causadesvinculacion;
    }

    public void setCausadesvinculacion(String causadesvinculacion) {
        this.causadesvinculacion = causadesvinculacion;
    }

    public String getRecomiendacargo() {
        return recomiendacargo;
    }

    public void setRecomiendacargo(String recomiendacargo) {
        this.recomiendacargo = recomiendacargo;
    }

    public String getPuntosdesarrollar() {
        return puntosdesarrollar;
    }

    public void setPuntosdesarrollar(String puntosdesarrollar) {
        this.puntosdesarrollar = puntosdesarrollar;
    }

    public String getDescripcioncomportamiento() {
        return descripcioncomportamiento;
    }

    public void setDescripcioncomportamiento(String descripcioncomportamiento) {
        this.descripcioncomportamiento = descripcioncomportamiento;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Recomendaciones getRecomendacion() {
        return recomendacion;
    }

    public void setRecomendacion(Recomendaciones recomendacion) {
        this.recomendacion = recomendacion;
    }

    public Experiencias getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(Experiencias experiencia) {
        this.experiencia = experiencia;
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
        if (!(object instanceof Verificacionesreferencias)) {
            return false;
        }
        Verificacionesreferencias other = (Verificacionesreferencias) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Verificacionesreferencias[ id=" + id + " ]";
    }
    
}
