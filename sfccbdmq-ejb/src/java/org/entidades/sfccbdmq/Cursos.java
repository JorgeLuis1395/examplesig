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
@Table(name = "cursos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Cursos.findAll", query = "SELECT c FROM Cursos c"),
    @NamedQuery(name = "Cursos.findById", query = "SELECT c FROM Cursos c WHERE c.id = :id"),
    @NamedQuery(name = "Cursos.findByNombre", query = "SELECT c FROM Cursos c WHERE c.nombre = :nombre"),
    @NamedQuery(name = "Cursos.findByEmpresa", query = "SELECT c FROM Cursos c WHERE c.empresa = :empresa"),
    @NamedQuery(name = "Cursos.findByDuracion", query = "SELECT c FROM Cursos c WHERE c.duracion = :duracion"),
    @NamedQuery(name = "Cursos.findByValidado", query = "SELECT c FROM Cursos c WHERE c.validado = :validado"),
    @NamedQuery(name = "Cursos.findByRecibido", query = "SELECT c FROM Cursos c WHERE c.recibido = :recibido"),
    @NamedQuery(name = "Cursos.findByFecha", query = "SELECT c FROM Cursos c WHERE c.fecha = :fecha"),
    @NamedQuery(name = "Cursos.findByResponvalid", query = "SELECT c FROM Cursos c WHERE c.responvalid = :responvalid"),
    @NamedQuery(name = "Cursos.findByObsvalidado", query = "SELECT c FROM Cursos c WHERE c.obsvalidado = :obsvalidado"),
    @NamedQuery(name = "Cursos.findByFechaingreso", query = "SELECT c FROM Cursos c WHERE c.fechaingreso = :fechaingreso")})
public class Cursos implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "nombre")
    private String nombre;
    @Size(max = 2147483647)
    @Column(name = "empresa")
    private String empresa;
    @Column(name = "duracion")
    private Integer duracion;
    @Column(name = "validado")
    private Boolean validado;
    @Column(name = "recibido")
    private Boolean recibido;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Size(max = 2147483647)
    @Column(name = "responvalid")
    private String responvalid;
    @Size(max = 2147483647)
    @Column(name = "obsvalidado")
    private String obsvalidado;
    @Column(name = "fechaingreso")
    @Temporal(TemporalType.DATE)
    private Date fechaingreso;
    @JoinColumn(name = "empleado", referencedColumnName = "id")
    @ManyToOne
    private Empleados empleado;

    public Cursos() {
    }

    public Cursos(Integer id) {
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

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public Integer getDuracion() {
        return duracion;
    }

    public void setDuracion(Integer duracion) {
        this.duracion = duracion;
    }

    public Boolean getValidado() {
        return validado;
    }

    public void setValidado(Boolean validado) {
        this.validado = validado;
    }

    public Boolean getRecibido() {
        return recibido;
    }

    public void setRecibido(Boolean recibido) {
        this.recibido = recibido;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getResponvalid() {
        return responvalid;
    }

    public void setResponvalid(String responvalid) {
        this.responvalid = responvalid;
    }

    public String getObsvalidado() {
        return obsvalidado;
    }

    public void setObsvalidado(String obsvalidado) {
        this.obsvalidado = obsvalidado;
    }

    public Date getFechaingreso() {
        return fechaingreso;
    }

    public void setFechaingreso(Date fechaingreso) {
        this.fechaingreso = fechaingreso;
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
        if (!(object instanceof Cursos)) {
            return false;
        }
        Cursos other = (Cursos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Cursos[ id=" + id + " ]";
    }
    
}
