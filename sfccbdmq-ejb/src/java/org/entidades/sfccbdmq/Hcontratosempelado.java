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
@Table(name = "hcontratosempelado")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Hcontratosempelado.findAll", query = "SELECT h FROM Hcontratosempelado h"),
    @NamedQuery(name = "Hcontratosempelado.findById", query = "SELECT h FROM Hcontratosempelado h WHERE h.id = :id"),
    @NamedQuery(name = "Hcontratosempelado.findByDesde", query = "SELECT h FROM Hcontratosempelado h WHERE h.desde = :desde"),
    @NamedQuery(name = "Hcontratosempelado.findByHasta", query = "SELECT h FROM Hcontratosempelado h WHERE h.hasta = :hasta"),
    @NamedQuery(name = "Hcontratosempelado.findByMotivo", query = "SELECT h FROM Hcontratosempelado h WHERE h.motivo = :motivo"),
    @NamedQuery(name = "Hcontratosempelado.findByFecha", query = "SELECT h FROM Hcontratosempelado h WHERE h.fecha = :fecha"),
    @NamedQuery(name = "Hcontratosempelado.findByUsuario", query = "SELECT h FROM Hcontratosempelado h WHERE h.usuario = :usuario")})
public class Hcontratosempelado implements Serializable {
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
    @Size(max = 2147483647)
    @Column(name = "motivo")
    private String motivo;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Size(max = 2147483647)
    @Column(name = "usuario")
    private String usuario;
    @JoinColumn(name = "tipocontrato", referencedColumnName = "id")
    @ManyToOne
    private Tiposcontratos tipocontrato;
    @JoinColumn(name = "empleado", referencedColumnName = "id")
    @ManyToOne
    private Empleados empleado;

    public Hcontratosempelado() {
    }

    public Hcontratosempelado(Integer id) {
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

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Tiposcontratos getTipocontrato() {
        return tipocontrato;
    }

    public void setTipocontrato(Tiposcontratos tipocontrato) {
        this.tipocontrato = tipocontrato;
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
        if (!(object instanceof Hcontratosempelado)) {
            return false;
        }
        Hcontratosempelado other = (Hcontratosempelado) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Hcontratosempelado[ id=" + id + " ]";
    }
    
}
