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
@Table(name = "tipomovbancos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tipomovbancos.findAll", query = "SELECT t FROM Tipomovbancos t"),
    @NamedQuery(name = "Tipomovbancos.findById", query = "SELECT t FROM Tipomovbancos t WHERE t.id = :id"),
    @NamedQuery(name = "Tipomovbancos.findByDescripcion", query = "SELECT t FROM Tipomovbancos t WHERE t.descripcion = :descripcion"),
    @NamedQuery(name = "Tipomovbancos.findByCuenta", query = "SELECT t FROM Tipomovbancos t WHERE t.cuenta = :cuenta"),
    @NamedQuery(name = "Tipomovbancos.findByFecha", query = "SELECT t FROM Tipomovbancos t WHERE t.fecha = :fecha"),
    @NamedQuery(name = "Tipomovbancos.findByTipo", query = "SELECT t FROM Tipomovbancos t WHERE t.tipo = :tipo")})
public class Tipomovbancos implements Serializable {

    @Column(name = "generacomprobante")
    private Boolean generacomprobante;
    @OneToMany(mappedBy = "tipo")
    private List<Ingresos> ingresosList;
    @OneToMany(mappedBy = "tipomov")
    private List<Kardexbanco> kardexbancoList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "descripcion")
    private String descripcion;
    @Size(max = 2147483647)
    @Column(name = "cuenta")
    private String cuenta;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Size(max = 10)
    @Column(name = "tipo")
    private String tipo;
    @JoinColumn(name = "tipoasiento", referencedColumnName = "id")
    @ManyToOne
    private Tipoasiento tipoasiento;
    @Column(name = "spi")
    private Boolean spi;
    @Column(name = "ingreso")
    private Boolean ingreso;
    public Tipomovbancos() {
    }

    public Tipomovbancos(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Tipoasiento getTipoasiento() {
        return tipoasiento;
    }

    public void setTipoasiento(Tipoasiento tipoasiento) {
        this.tipoasiento = tipoasiento;
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
        if (!(object instanceof Tipomovbancos)) {
            return false;
        }
        Tipomovbancos other = (Tipomovbancos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return descripcion;
    }

    @XmlTransient
    public List<Kardexbanco> getKardexbancoList() {
        return kardexbancoList;
    }

    public void setKardexbancoList(List<Kardexbanco> kardexbancoList) {
        this.kardexbancoList = kardexbancoList;
    }

    /**
     * @return the spi
     */
    public Boolean getSpi() {
        return spi;
    }

    /**
     * @param spi the spi to set
     */
    public void setSpi(Boolean spi) {
        this.spi = spi;
    }

    /**
     * @return the ingreso
     */
    public Boolean getIngreso() {
        return ingreso;
    }

    /**
     * @param ingreso the ingreso to set
     */
    public void setIngreso(Boolean ingreso) {
        this.ingreso = ingreso;
    }

    @XmlTransient
    public List<Ingresos> getIngresosList() {
        return ingresosList;
    }

    public void setIngresosList(List<Ingresos> ingresosList) {
        this.ingresosList = ingresosList;
    }

    public Boolean getGeneracomprobante() {
        return generacomprobante;
    }

    public void setGeneracomprobante(Boolean generacomprobante) {
        this.generacomprobante = generacomprobante;
    }
    
}
