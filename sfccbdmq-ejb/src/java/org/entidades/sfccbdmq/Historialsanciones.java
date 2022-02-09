/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "historialsanciones")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Historialsanciones.findAll", query = "SELECT h FROM Historialsanciones h"),
    @NamedQuery(name = "Historialsanciones.findById", query = "SELECT h FROM Historialsanciones h WHERE h.id = :id"),
    @NamedQuery(name = "Historialsanciones.findByMotivo", query = "SELECT h FROM Historialsanciones h WHERE h.motivo = :motivo"),
    @NamedQuery(name = "Historialsanciones.findByFaplicacion", query = "SELECT h FROM Historialsanciones h WHERE h.faplicacion = :faplicacion"),
    @NamedQuery(name = "Historialsanciones.findByFaprobacion", query = "SELECT h FROM Historialsanciones h WHERE h.faprobacion = :faprobacion"),
    @NamedQuery(name = "Historialsanciones.findByFsancion", query = "SELECT h FROM Historialsanciones h WHERE h.fsancion = :fsancion"),
    @NamedQuery(name = "Historialsanciones.findByValor", query = "SELECT h FROM Historialsanciones h WHERE h.valor = :valor")})
public class Historialsanciones implements Serializable {
    @OneToMany(mappedBy = "sancion")
    private List<Pagosempleados> pagosempleadosList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "motivo")
    private String motivo;
    @Column(name = "especunaria")
    private Boolean especunaria;
    @Column(name = "esleve")
    private Boolean esleve;
    @Column(name = "faplicacion")
    @Temporal(TemporalType.DATE)
    private Date faplicacion;
    @Column(name = "faprobacion")
    @Temporal(TemporalType.DATE)
    private Date faprobacion;
    @Column(name = "fsancion")
    @Temporal(TemporalType.DATE)
    private Date fsancion;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    @JoinColumn(name = "tipo", referencedColumnName = "id")
    @ManyToOne
    private Tiposanciones tipo;
    @JoinColumn(name = "empleado", referencedColumnName = "id")
    @ManyToOne
    private Empleados empleado;
    @Transient
    private double porcentaje;
    public Historialsanciones() {
    }

    public Historialsanciones(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Date getFaplicacion() {
        return faplicacion;
    }

    public void setFaplicacion(Date faplicacion) {
        this.faplicacion = faplicacion;
    }

    public Date getFaprobacion() {
        return faprobacion;
    }

    public void setFaprobacion(Date faprobacion) {
        this.faprobacion = faprobacion;
    }

    public Date getFsancion() {
        return fsancion;
    }

    public void setFsancion(Date fsancion) {
        this.fsancion = fsancion;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Tiposanciones getTipo() {
        return tipo;
    }

    public void setTipo(Tiposanciones tipo) {
        this.tipo = tipo;
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
        if (!(object instanceof Historialsanciones)) {
            return false;
        }
        Historialsanciones other = (Historialsanciones) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Historialsanciones[ id=" + id + " ]";
    }

    /**
     * @return the especunaria
     */
    public Boolean getEspecunaria() {
        return especunaria;
    }

    /**
     * @param especunaria the especunaria to set
     */
    public void setEspecunaria(Boolean especunaria) {
        this.especunaria = especunaria;
    }

    /**
     * @return the esleve
     */
    public Boolean getEsleve() {
        return esleve;
    }

    /**
     * @param esleve the esleve to set
     */
    public void setEsleve(Boolean esleve) {
        this.esleve = esleve;
    }

    @XmlTransient
    public List<Pagosempleados> getPagosempleadosList() {
        return pagosempleadosList;
    }

    public void setPagosempleadosList(List<Pagosempleados> pagosempleadosList) {
        this.pagosempleadosList = pagosempleadosList;
    }

    /**
     * @return the porcentaje
     */
    public double getPorcentaje() {
        return porcentaje;
    }

    /**
     * @param porcentaje the porcentaje to set
     */
    public void setPorcentaje(double porcentaje) {
        this.porcentaje = porcentaje;
    }
    
}
