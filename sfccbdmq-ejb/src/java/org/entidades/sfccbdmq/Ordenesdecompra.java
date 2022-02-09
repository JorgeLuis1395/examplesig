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
@Table(name = "ordenesdecompra")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Ordenesdecompra.findAll", query = "SELECT o FROM Ordenesdecompra o"),
    @NamedQuery(name = "Ordenesdecompra.findById", query = "SELECT o FROM Ordenesdecompra o WHERE o.id = :id"),
    @NamedQuery(name = "Ordenesdecompra.findByFecha", query = "SELECT o FROM Ordenesdecompra o WHERE o.fecha = :fecha"),
    @NamedQuery(name = "Ordenesdecompra.findByFechaelaboracion", query = "SELECT o FROM Ordenesdecompra o WHERE o.fechaelaboracion = :fechaelaboracion"),
    @NamedQuery(name = "Ordenesdecompra.findByFechadefinitiva", query = "SELECT o FROM Ordenesdecompra o WHERE o.fechadefinitiva = :fechadefinitiva"),
    @NamedQuery(name = "Ordenesdecompra.findByObservaciones", query = "SELECT o FROM Ordenesdecompra o WHERE o.observaciones = :observaciones")})
public class Ordenesdecompra implements Serializable {
    @JoinColumn(name = "proveedor", referencedColumnName = "id")
    @ManyToOne
    private Proveedores proveedor;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Column(name = "fechaelaboracion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaelaboracion;
    @Column(name = "fechadefinitiva")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechadefinitiva;
    @Size(max = 2147483647)
    @Column(name = "observaciones")
    private String observaciones;
    @Column(name = "factura")
    private Integer factura;
    @OneToMany(mappedBy = "ordencompra")
    private List<Cabecerainventario> cabecerainventarioList;
    @JoinColumn(name = "empleado", referencedColumnName = "id")
    @ManyToOne
    private Empleados empleado;
    @JoinColumn(name = "compromiso", referencedColumnName = "id")
    @ManyToOne
    private Compromisos compromiso;
    @OneToMany(mappedBy = "ordencompra")
    private List<Detalleorden> detalleordenList;

    public Ordenesdecompra() {
    }

    public Ordenesdecompra(Integer id) {
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

    public Date getFechaelaboracion() {
        return fechaelaboracion;
    }

    public void setFechaelaboracion(Date fechaelaboracion) {
        this.fechaelaboracion = fechaelaboracion;
    }

    public Date getFechadefinitiva() {
        return fechadefinitiva;
    }

    public void setFechadefinitiva(Date fechadefinitiva) {
        this.fechadefinitiva = fechadefinitiva;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @XmlTransient
    public List<Cabecerainventario> getCabecerainventarioList() {
        return cabecerainventarioList;
    }

    public void setCabecerainventarioList(List<Cabecerainventario> cabecerainventarioList) {
        this.cabecerainventarioList = cabecerainventarioList;
    }

    public Empleados getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleados empleado) {
        this.empleado = empleado;
    }

    public Compromisos getCompromiso() {
        return compromiso;
    }

    public void setCompromiso(Compromisos compromiso) {
        this.compromiso = compromiso;
    }

    @XmlTransient
    public List<Detalleorden> getDetalleordenList() {
        return detalleordenList;
    }

    public void setDetalleordenList(List<Detalleorden> detalleordenList) {
        this.detalleordenList = detalleordenList;
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
        if (!(object instanceof Ordenesdecompra)) {
            return false;
        }
        Ordenesdecompra other = (Ordenesdecompra) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Ordenesdecompra[ id=" + id + " ]";
    }

    public Proveedores getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedores proveedor) {
        this.proveedor = proveedor;
    }

    /**
     * @return the factura
     */
    public Integer getFactura() {
        return factura;
    }

    /**
     * @param factura the factura to set
     */
    public void setFactura(Integer factura) {
        this.factura = factura;
    }
    
}
