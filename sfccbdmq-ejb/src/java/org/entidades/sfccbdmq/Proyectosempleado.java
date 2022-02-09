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
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "proyectosempleado")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Proyectosempleado.findAll", query = "SELECT p FROM Proyectosempleado p"),
    @NamedQuery(name = "Proyectosempleado.findById", query = "SELECT p FROM Proyectosempleado p WHERE p.id = :id"),
    @NamedQuery(name = "Proyectosempleado.findByNombre", query = "SELECT p FROM Proyectosempleado p WHERE p.nombre = :nombre"),
    @NamedQuery(name = "Proyectosempleado.findByActividades", query = "SELECT p FROM Proyectosempleado p WHERE p.actividades = :actividades"),
    @NamedQuery(name = "Proyectosempleado.findByDesde", query = "SELECT p FROM Proyectosempleado p WHERE p.desde = :desde"),
    @NamedQuery(name = "Proyectosempleado.findByHasta", query = "SELECT p FROM Proyectosempleado p WHERE p.hasta = :hasta")})
public class Proyectosempleado implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "nombre")
    private String nombre;
    @Size(max = 2147483647)
    @Column(name = "actividades")
    private String actividades;
    @Column(name = "desde")
    @Temporal(TemporalType.DATE)
    private Date desde;
    @Column(name = "hasta")
    @Temporal(TemporalType.DATE)
    private Date hasta;
    @JoinColumn(name = "empleado", referencedColumnName = "id")
    @ManyToOne
    private Empleados empleado;

    public Proyectosempleado() {
    }

    public Proyectosempleado(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getActividades() {
        return actividades;
    }

    public void setActividades(String actividades) {
        this.actividades = actividades;
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
        if (!(object instanceof Proyectosempleado)) {
            return false;
        }
        Proyectosempleado other = (Proyectosempleado) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Proyectosempleado[ id=" + id + " ]";
    }
    
}
