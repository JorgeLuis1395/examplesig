/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "organigramasuministros")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Organigramasuministros.findAll", query = "SELECT o FROM Organigramasuministros o"),
    @NamedQuery(name = "Organigramasuministros.findById", query = "SELECT o FROM Organigramasuministros o WHERE o.id = :id"),
    @NamedQuery(name = "Organigramasuministros.findByAnio", query = "SELECT o FROM Organigramasuministros o WHERE o.anio = :anio"),
    @NamedQuery(name = "Organigramasuministros.findByCantidad", query = "SELECT o FROM Organigramasuministros o WHERE o.cantidad = :cantidad"),
    @NamedQuery(name = "Organigramasuministros.findByExplicacion", query = "SELECT o FROM Organigramasuministros o WHERE o.explicacion = :explicacion"),
    @NamedQuery(name = "Organigramasuministros.findByAprobado", query = "SELECT o FROM Organigramasuministros o WHERE o.aprobado = :aprobado")})
public class Organigramasuministros implements Serializable {
    @OneToMany(mappedBy = "organigrama")
    private List<Empleadosuministros> empleadosuministrosList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "anio")
    private Integer anio;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "cantidad")
    private Float cantidad;
    @Size(max = 2147483647)
    @Column(name = "explicacion")
    private String explicacion;
    @Column(name = "aprobado")
    private Float aprobado;
    @JoinColumn(name = "suministro", referencedColumnName = "id")
    @ManyToOne
    private Suministros suministro;
    @JoinColumn(name = "oficina", referencedColumnName = "id")
    @ManyToOne
    private Oficinas oficina;
    @JoinColumn(name = "empleado", referencedColumnName = "id")
    @ManyToOne
    private Empleados empleado;

    public Organigramasuministros() {
    }

    public Organigramasuministros(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Float getCantidad() {
        return cantidad;
    }

    public void setCantidad(Float cantidad) {
        this.cantidad = cantidad;
    }

    public String getExplicacion() {
        return explicacion;
    }

    public void setExplicacion(String explicacion) {
        this.explicacion = explicacion;
    }

    public Float getAprobado() {
        return aprobado;
    }

    public void setAprobado(Float aprobado) {
        this.aprobado = aprobado;
    }

    public Suministros getSuministro() {
        return suministro;
    }

    public void setSuministro(Suministros suministro) {
        this.suministro = suministro;
    }

    public Oficinas getOficina() {
        return oficina;
    }

    public void setOficina(Oficinas oficina) {
        this.oficina = oficina;
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
        if (!(object instanceof Organigramasuministros)) {
            return false;
        }
        Organigramasuministros other = (Organigramasuministros) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Organigramasuministros[ id=" + id + " ]";
    }

    @XmlTransient
    public List<Empleadosuministros> getEmpleadosuministrosList() {
        return empleadosuministrosList;
    }

    public void setEmpleadosuministrosList(List<Empleadosuministros> empleadosuministrosList) {
        this.empleadosuministrosList = empleadosuministrosList;
    }
    
}
