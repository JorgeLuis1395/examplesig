/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
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
@Table(name = "reformaspoa")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Reformaspoa.findAll", query = "SELECT r FROM Reformaspoa r")
    , @NamedQuery(name = "Reformaspoa.findById", query = "SELECT r FROM Reformaspoa r WHERE r.id = :id")
    , @NamedQuery(name = "Reformaspoa.findByFecha", query = "SELECT r FROM Reformaspoa r WHERE r.fecha = :fecha")
    , @NamedQuery(name = "Reformaspoa.findByValor", query = "SELECT r FROM Reformaspoa r WHERE r.valor = :valor")})
public class Reformaspoa implements Serializable {

    @Column(name = "aprobado")
    private Boolean aprobado;

    @Size(max = 2147483647)
    @Column(name = "nombreproyecto")
    private String nombreproyecto;

    @JoinColumn(name = "proyecto", referencedColumnName = "id")
    @ManyToOne
    private Proyectospoa proyecto;

    @Size(max = 2147483647)
    @Column(name = "requerimiento")
    private String requerimiento;
    @Size(max = 2147483647)
    @Column(name = "documento")
    private String documento;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    @JoinColumn(name = "asignacion", referencedColumnName = "id")
    @ManyToOne
    private Asignacionespoa asignacion;
    @JoinColumn(name = "cabecera", referencedColumnName = "id")
    @ManyToOne
    private Cabecerareformaspoa cabecera;

    @Transient
    private double totalSubactividad;
    @Transient
    private double totalReforma;
    @Transient
    private double totalTotal;

    public Reformaspoa() {
    }

    public Reformaspoa(Integer id) {
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

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Cabecerareformaspoa getCabecera() {
        return cabecera;
    }

    public void setCabecera(Cabecerareformaspoa cabecera) {
        this.cabecera = cabecera;
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
        if (!(object instanceof Reformaspoa)) {
            return false;
        }
        Reformaspoa other = (Reformaspoa) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.pacpoaiepi.Reformaspoa[ id=" + id + " ]";
    }

    /**
     * @return the totalSubactividad
     */
    public double getTotalSubactividad() {
        return totalSubactividad;
    }

    /**
     * @param totalSubactividad the totalSubactividad to set
     */
    public void setTotalSubactividad(double totalSubactividad) {
        this.totalSubactividad = totalSubactividad;
    }

    /**
     * @return the totalReforma
     */
    public double getTotalReforma() {
        return totalReforma;
    }

    /**
     * @param totalReforma the totalReforma to set
     */
    public void setTotalReforma(double totalReforma) {
        this.totalReforma = totalReforma;
    }

    /**
     * @return the totalTotal
     */
    public double getTotalTotal() {
        return totalTotal;
    }

    /**
     * @param totalTotal the totalTotal to set
     */
    public void setTotalTotal(double totalTotal) {
        this.totalTotal = totalTotal;
    }

    public String getRequerimiento() {
        return requerimiento;
    }

    public void setRequerimiento(String requerimiento) {
        this.requerimiento = requerimiento;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public Proyectospoa getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyectospoa proyecto) {
        this.proyecto = proyecto;
    }
    
    
    public Asignacionespoa getAsignacion() {
        return asignacion;
    }

    public void setAsignacion(Asignacionespoa asignacion) {
        this.asignacion = asignacion;
    }

    public String getNombreproyecto() {
        return nombreproyecto;
    }

    public void setNombreproyecto(String nombreproyecto) {
        this.nombreproyecto = nombreproyecto;
    }

    public Boolean getAprobado() {
        return aprobado;
    }

    public void setAprobado(Boolean aprobado) {
        this.aprobado = aprobado;
    }

}