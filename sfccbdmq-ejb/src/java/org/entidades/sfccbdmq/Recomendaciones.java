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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
 * @author edwin
 */
@Entity
@Table(name = "recomendaciones")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Recomendaciones.findAll", query = "SELECT r FROM Recomendaciones r"),
    @NamedQuery(name = "Recomendaciones.findById", query = "SELECT r FROM Recomendaciones r WHERE r.id = :id"),
    @NamedQuery(name = "Recomendaciones.findByNombre", query = "SELECT r FROM Recomendaciones r WHERE r.nombre = :nombre"),
    @NamedQuery(name = "Recomendaciones.findByTelefono", query = "SELECT r FROM Recomendaciones r WHERE r.telefono = :telefono"),
    @NamedQuery(name = "Recomendaciones.findByCargo", query = "SELECT r FROM Recomendaciones r WHERE r.cargo = :cargo"),
    @NamedQuery(name = "Recomendaciones.findByEmpresa", query = "SELECT r FROM Recomendaciones r WHERE r.empresa = :empresa")})
public class Recomendaciones implements Serializable {
    @Column(name = "fechaingreso")
    @Temporal(TemporalType.DATE)
    private Date fechaingreso;
    @Column(name = "validado")
    private Boolean validado;
    @Size(max = 2147483647)
    @Column(name = "responvalid")
    private String responvalid;
    @Size(max = 2147483647)
    @Column(name = "obsvalidado")
    private String obsvalidado;
    @OneToMany(mappedBy = "recomendacion")
    private List<Verificacionesreferencias> verificacionesreferenciasList;
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
    @Column(name = "telefono")
    private String telefono;
    @Size(max = 2147483647)
    @Column(name = "cargo")
    private String cargo;
    @Size(max = 2147483647)
    @Column(name = "empresa")
    private String empresa;
    @JoinColumn(name = "empleado", referencedColumnName = "id")
    @ManyToOne
    private Empleados empleado;

    public Recomendaciones() {
    }

    public Recomendaciones(Integer id) {
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
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
        if (!(object instanceof Recomendaciones)) {
            return false;
        }
        Recomendaciones other = (Recomendaciones) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Recomendaciones[ id=" + id + " ]";
    }

    public Boolean getValidado() {
        return validado;
    }

    public void setValidado(Boolean validado) {
        this.validado = validado;
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

    @XmlTransient
    public List<Verificacionesreferencias> getVerificacionesreferenciasList() {
        return verificacionesreferenciasList;
    }

    public void setVerificacionesreferenciasList(List<Verificacionesreferencias> verificacionesreferenciasList) {
        this.verificacionesreferenciasList = verificacionesreferenciasList;
    }

    /**
     * @return the fechaingreso
     */
    public Date getFechaingreso() {
        return fechaingreso;
    }

    /**
     * @param fechaingreso the fechaingreso to set
     */
    public void setFechaingreso(Date fechaingreso) {
        this.fechaingreso = fechaingreso;
    }
    
}
