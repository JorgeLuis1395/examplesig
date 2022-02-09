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
@Table(name = "solicitudsuministros")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Solicitudsuministros.findAll", query = "SELECT s FROM Solicitudsuministros s"),
    @NamedQuery(name = "Solicitudsuministros.findById", query = "SELECT s FROM Solicitudsuministros s WHERE s.id = :id"),
    @NamedQuery(name = "Solicitudsuministros.findByFecha", query = "SELECT s FROM Solicitudsuministros s WHERE s.fecha = :fecha"),
    @NamedQuery(name = "Solicitudsuministros.findByDespacho", query = "SELECT s FROM Solicitudsuministros s WHERE s.despacho = :despacho"),
    @NamedQuery(name = "Solicitudsuministros.findByObservaciones", query = "SELECT s FROM Solicitudsuministros s WHERE s.observaciones = :observaciones")})
public class Solicitudsuministros implements Serializable {

    @Column(name = "oficina")
    private Integer oficina;
    @JoinColumn(name = "direccionaprueba", referencedColumnName = "id")
    @ManyToOne
    private Empleados direccionaprueba;
    @OneToMany(mappedBy = "solicitud")
    private List<Cabecerainventario> cabecerainventarioList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Column(name = "despacho")
    @Temporal(TemporalType.TIMESTAMP)
    private Date despacho;
    @Size(max = 2147483647)
    @Column(name = "observaciones")
    private String observaciones;
    @OneToMany(mappedBy = "solicitudsuministro")
    private List<Detallesolicitud> detallesolicitudList;
    @JoinColumn(name = "empleadosolicita", referencedColumnName = "id")
    @ManyToOne
    private Empleados empleadosolicita;
    @JoinColumn(name = "empleadodespacho", referencedColumnName = "id")
    @ManyToOne
    private Empleados empleadodespacho;

    public Solicitudsuministros() {
    }

    public Solicitudsuministros(Integer id) {
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

    public Date getDespacho() {
        return despacho;
    }

    public void setDespacho(Date despacho) {
        this.despacho = despacho;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @XmlTransient
    public List<Detallesolicitud> getDetallesolicitudList() {
        return detallesolicitudList;
    }

    public void setDetallesolicitudList(List<Detallesolicitud> detallesolicitudList) {
        this.detallesolicitudList = detallesolicitudList;
    }

    public Empleados getEmpleadosolicita() {
        return empleadosolicita;
    }

    public void setEmpleadosolicita(Empleados empleadosolicita) {
        this.empleadosolicita = empleadosolicita;
    }

    public Empleados getEmpleadodespacho() {
        return empleadodespacho;
    }

    public void setEmpleadodespacho(Empleados empleadodespacho) {
        this.empleadodespacho = empleadodespacho;
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
        if (!(object instanceof Solicitudsuministros)) {
            return false;
        }
        Solicitudsuministros other = (Solicitudsuministros) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Solicitudsuministros[ id=" + id + " ]";
    }

    @XmlTransient
    public List<Cabecerainventario> getCabecerainventarioList() {
        return cabecerainventarioList;
    }

    public void setCabecerainventarioList(List<Cabecerainventario> cabecerainventarioList) {
        this.cabecerainventarioList = cabecerainventarioList;
    }

    public Integer getOficina() {
        return oficina;
    }

    public void setOficina(Integer oficina) {
        this.oficina = oficina;
    }

    public Empleados getDireccionaprueba() {
        return direccionaprueba;
    }

    public void setDireccionaprueba(Empleados direccionaprueba) {
        this.direccionaprueba = direccionaprueba;
    }
    
}
