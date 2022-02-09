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
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "pagosnp")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Pagosnp.findAll", query = "SELECT p FROM Pagosnp p")
    , @NamedQuery(name = "Pagosnp.findById", query = "SELECT p FROM Pagosnp p WHERE p.id = :id")
    , @NamedQuery(name = "Pagosnp.findByDescripcion", query = "SELECT p FROM Pagosnp p WHERE p.descripcion = :descripcion")
    , @NamedQuery(name = "Pagosnp.findByFecha", query = "SELECT p FROM Pagosnp p WHERE p.fecha = :fecha")
    , @NamedQuery(name = "Pagosnp.findByContabilizacion", query = "SELECT p FROM Pagosnp p WHERE p.contabilizacion = :contabilizacion")
    , @NamedQuery(name = "Pagosnp.findByUsuario", query = "SELECT p FROM Pagosnp p WHERE p.usuario = :usuario")})
public class Pagosnp implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Column(name = "contabilizacion")
    @Temporal(TemporalType.DATE)
    private Date contabilizacion;
    @Size(max = 2147483647)
    @Column(name = "usuario")
    private String usuario;
    @OneToMany(mappedBy = "pagonp")
    private List<Pagosvencimientos> pagosvencimientosList;
    @JoinColumn(name = "cuentas", referencedColumnName = "id")
    @ManyToOne
    private Codigos cuentas;
    @JoinColumn(name = "proveedor", referencedColumnName = "id")
    @ManyToOne
    private Proveedores proveedor;
    @OneToMany(mappedBy = "pagonp")
    private List<Auxiliaresnp> auxiliaresnpList;

    public Pagosnp() {
    }

    public Pagosnp(Integer id) {
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

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Date getContabilizacion() {
        return contabilizacion;
    }

    public void setContabilizacion(Date contabilizacion) {
        this.contabilizacion = contabilizacion;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    @XmlTransient
    @JsonIgnore
    public List<Pagosvencimientos> getPagosvencimientosList() {
        return pagosvencimientosList;
    }

    public void setPagosvencimientosList(List<Pagosvencimientos> pagosvencimientosList) {
        this.pagosvencimientosList = pagosvencimientosList;
    }

    public Codigos getCuentas() {
        return cuentas;
    }

    public void setCuentas(Codigos cuentas) {
        this.cuentas = cuentas;
    }

    public Proveedores getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedores proveedor) {
        this.proveedor = proveedor;
    }

    @XmlTransient
    @JsonIgnore
    public List<Auxiliaresnp> getAuxiliaresnpList() {
        return auxiliaresnpList;
    }

    public void setAuxiliaresnpList(List<Auxiliaresnp> auxiliaresnpList) {
        this.auxiliaresnpList = auxiliaresnpList;
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
        if (!(object instanceof Pagosnp)) {
            return false;
        }
        Pagosnp other = (Pagosnp) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.sfccbdmq.Pagosnp[ id=" + id + " ]";
    }
    
}
