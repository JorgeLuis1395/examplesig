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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "planificacionvacaciones")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Planificacionvacaciones.findAll", query = "SELECT p FROM Planificacionvacaciones p")
    , @NamedQuery(name = "Planificacionvacaciones.findById", query = "SELECT p FROM Planificacionvacaciones p WHERE p.id = :id")
    , @NamedQuery(name = "Planificacionvacaciones.findByDesde", query = "SELECT p FROM Planificacionvacaciones p WHERE p.desde = :desde")
    , @NamedQuery(name = "Planificacionvacaciones.findByHasta", query = "SELECT p FROM Planificacionvacaciones p WHERE p.hasta = :hasta")})
public class Planificacionvacaciones implements Serializable {

    @Column(name = "aprobado")
    @Temporal(TemporalType.DATE)
    private Date aprobado;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "desde")
    @Temporal(TemporalType.DATE)
    private Date desde;
    @Column(name = "hasta")
    @Temporal(TemporalType.DATE)
    private Date hasta;
    @JoinColumn(name = "empleado", referencedColumnName = "id")
    @ManyToOne
    private Empleados empleado;
    @Column(name = "observaciones")
    private String observaciones;
    @Transient
    private String cedula;
    @Transient
    private String desdeString;
    @Transient
    private String hastaString;
    public Planificacionvacaciones() {
    }

    public Planificacionvacaciones(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDesde() {
        return desde;
    }

    public void setDesde(Date desde) {
        this.desde = desde;
    }

    public Date getHasta() {
        return hasta;
    }

    public void setHasta(Date hasta) {
        this.hasta = hasta;
    }

    public Empleados getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleados empleado) {
        this.empleado = empleado;
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
        if (!(object instanceof Planificacionvacaciones)) {
            return false;
        }
        Planificacionvacaciones other = (Planificacionvacaciones) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.sfccbdmq.Planificacionvacaciones[ id=" + id + " ]";
    }

    /**
     * @return the observaciones
     */
    public String getObservaciones() {
        return observaciones;
    }

    /**
     * @param observaciones the observaciones to set
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    /**
     * @return the cedula
     */
    public String getCedula() {
        return cedula;
    }

    /**
     * @param cedula the cedula to set
     */
    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    /**
     * @return the desdeString
     */
    public String getDesdeString() {
        return desdeString;
    }

    /**
     * @param desdeString the desdeString to set
     */
    public void setDesdeString(String desdeString) {
        this.desdeString = desdeString;
    }

    /**
     * @return the hastaString
     */
    public String getHastaString() {
        return hastaString;
    }

    /**
     * @param hastaString the hastaString to set
     */
    public void setHastaString(String hastaString) {
        this.hastaString = hastaString;
    }

    public Date getAprobado() {
        return aprobado;
    }

    public void setAprobado(Date aprobado) {
        this.aprobado = aprobado;
    }
    
}
