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
@Table(name = "empleadosuministros")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Empleadosuministros.findAll", query = "SELECT e FROM Empleadosuministros e"),
    @NamedQuery(name = "Empleadosuministros.findById", query = "SELECT e FROM Empleadosuministros e WHERE e.id = :id"),
    @NamedQuery(name = "Empleadosuministros.findByAnio", query = "SELECT e FROM Empleadosuministros e WHERE e.anio = :anio"),
    @NamedQuery(name = "Empleadosuministros.findByCantidad", query = "SELECT e FROM Empleadosuministros e WHERE e.cantidad = :cantidad"),
    @NamedQuery(name = "Empleadosuministros.findByExplicacion", query = "SELECT e FROM Empleadosuministros e WHERE e.explicacion = :explicacion"),
    @NamedQuery(name = "Empleadosuministros.findByAprobado", query = "SELECT e FROM Empleadosuministros e WHERE e.aprobado = :aprobado"),
    @NamedQuery(name = "Empleadosuministros.findByTrimestre", query = "SELECT e FROM Empleadosuministros e WHERE e.trimestre = :trimestre"),
    @NamedQuery(name = "Empleadosuministros.findByFautorizado", query = "SELECT e FROM Empleadosuministros e WHERE e.fautorizado = :fautorizado"),
    @NamedQuery(name = "Empleadosuministros.findByFaprovado", query = "SELECT e FROM Empleadosuministros e WHERE e.faprovado = :faprovado")})
public class Empleadosuministros implements Serializable {
    @Column(name = "aprobadoinv")
    private Float aprobadoinv;
    @Column(name = "cantidadinv")
    private Float cantidadinv;
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
    @Column(name = "trimestre")
    private Integer trimestre;
    @Column(name = "fautorizado")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fautorizado;
    @Column(name = "faprovado")
    @Temporal(TemporalType.TIMESTAMP)
    private Date faprovado;
    @JoinColumn(name = "suministro", referencedColumnName = "id")
    @ManyToOne
    private Suministros suministro;
    @JoinColumn(name = "organigrama", referencedColumnName = "id")
    @ManyToOne
    private Organigramasuministros organigrama;
    @JoinColumn(name = "empleado", referencedColumnName = "id")
    @ManyToOne
    private Empleados empleado;
    @JoinColumn(name = "autorizador", referencedColumnName = "id")
    @ManyToOne
    private Empleados autorizador;
    @JoinColumn(name = "aprobador", referencedColumnName = "id")
    @ManyToOne
    private Empleados aprobador;

    public Empleadosuministros() {
    }

    public Empleadosuministros(Integer id) {
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

    public Integer getTrimestre() {
        return trimestre;
    }

    public void setTrimestre(Integer trimestre) {
        this.trimestre = trimestre;
    }

    public Date getFautorizado() {
        return fautorizado;
    }

    public void setFautorizado(Date fautorizado) {
        this.fautorizado = fautorizado;
    }

    public Date getFaprovado() {
        return faprovado;
    }

    public void setFaprovado(Date faprovado) {
        this.faprovado = faprovado;
    }

    public Suministros getSuministro() {
        return suministro;
    }

    public void setSuministro(Suministros suministro) {
        this.suministro = suministro;
    }

    public Organigramasuministros getOrganigrama() {
        return organigrama;
    }

    public void setOrganigrama(Organigramasuministros organigrama) {
        this.organigrama = organigrama;
    }

    public Empleados getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleados empleado) {
        this.empleado = empleado;
    }

    public Empleados getAutorizador() {
        return autorizador;
    }

    public void setAutorizador(Empleados autorizador) {
        this.autorizador = autorizador;
    }

    public Empleados getAprobador() {
        return aprobador;
    }

    public void setAprobador(Empleados aprobador) {
        this.aprobador = aprobador;
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
        if (!(object instanceof Empleadosuministros)) {
            return false;
        }
        Empleadosuministros other = (Empleadosuministros) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Empleadosuministros[ id=" + id + " ]";
    }

    public Float getCantidadinv() {
        return cantidadinv;
    }

    public void setCantidadinv(Float cantidadinv) {
        this.cantidadinv = cantidadinv;
    }

    public Float getAprobadoinv() {
        return aprobadoinv;
    }

    public void setAprobadoinv(Float aprobadoinv) {
        this.aprobadoinv = aprobadoinv;
    }
    
}
