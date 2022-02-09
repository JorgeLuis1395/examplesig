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
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author luis
 */
@Entity
@Table(name = "trackingspoa")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Trackingspoa.findAll", query = "SELECT t FROM Trackingspoa t")
    , @NamedQuery(name = "Trackingspoa.findById", query = "SELECT t FROM Trackingspoa t WHERE t.id = :id")
    , @NamedQuery(name = "Trackingspoa.findByFecha", query = "SELECT t FROM Trackingspoa t WHERE t.fecha = :fecha")
    , @NamedQuery(name = "Trackingspoa.findByObservaciones", query = "SELECT t FROM Trackingspoa t WHERE t.observaciones = :observaciones")
    , @NamedQuery(name = "Trackingspoa.findByResponsable", query = "SELECT t FROM Trackingspoa t WHERE t.responsable = :responsable")
    , @NamedQuery(name = "Trackingspoa.findByReformanombre", query = "SELECT t FROM Trackingspoa t WHERE t.reformanombre = :reformanombre")})
public class Trackingspoa implements Serializable {

    @Column(name = "utilizado")
    private Boolean utilizado;

    @Column(name = "aprobadopac")
    private Boolean aprobadopac;

    @Column(name = "reformapac")
    private Integer reformapac;

    @JoinColumn(name = "asignacion", referencedColumnName = "id")
    @ManyToOne
    private Asignacionespoa asignacion;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Size(max = 2147483647)
    @Column(name = "observaciones")
    private String observaciones;
    @Size(max = 2147483647)
    @Column(name = "responsable")
    private String responsable;
    @Column(name = "reformanombre")
    private Boolean reformanombre;
    @JoinColumn(name = "reforma", referencedColumnName = "id")
    @ManyToOne
    private Cabecerareformaspoa reforma;
    @JoinColumn(name = "certificacion", referencedColumnName = "id")
    @ManyToOne
    private Certificacionespoa certificacion;
    @JoinColumn(name = "proyecto", referencedColumnName = "id")
    @ManyToOne
    private Proyectospoa proyecto;
    @Transient
    private boolean director;
    
    public Trackingspoa() {
    }

    public Trackingspoa(Integer id) {
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

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public Boolean getReformanombre() {
        return reformanombre;
    }

    public void setReformanombre(Boolean reformanombre) {
        this.reformanombre = reformanombre;
    }

    public Cabecerareformaspoa getReforma() {
        return reforma;
    }

    public void setReforma(Cabecerareformaspoa reforma) {
        this.reforma = reforma;
    }

    public Certificacionespoa getCertificacion() {
        return certificacion;
    }

    public void setCertificacion(Certificacionespoa certificacion) {
        this.certificacion = certificacion;
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
        if (!(object instanceof Trackingspoa)) {
            return false;
        }
        Trackingspoa other = (Trackingspoa) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.pacpoaiepi.Trackingspoa[ id=" + id + " ]";
    }

    public Asignacionespoa getAsignacion() {
        return asignacion;
    }

    public void setAsignacion(Asignacionespoa asignacion) {
        this.asignacion = asignacion;
    }

    public Integer getReformapac() {
        return reformapac;
    }

    public void setReformapac(Integer reformapac) {
        this.reformapac = reformapac;
    }

    public Boolean getAprobadopac() {
        return aprobadopac;
    }

    public void setAprobadopac(Boolean aprobadopac) {
        this.aprobadopac = aprobadopac;
    }

    /**
     * @return the director
     */
    public boolean getDirector() {
        return director;
    }

    /**
     * @param director the director to set
     */
    public void setDirector(boolean director) {
        this.director = director;
    }

    public Boolean getUtilizado() {
        return utilizado;
    }

    public void setUtilizado(Boolean utilizado) {
        this.utilizado = utilizado;
    }
    
}
