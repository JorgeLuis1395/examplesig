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
@Table(name = "tipoajuste")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tipoajuste.findAll", query = "SELECT t FROM Tipoajuste t"),
    @NamedQuery(name = "Tipoajuste.findById", query = "SELECT t FROM Tipoajuste t WHERE t.id = :id"),
    @NamedQuery(name = "Tipoajuste.findByNombre", query = "SELECT t FROM Tipoajuste t WHERE t.nombre = :nombre"),
    @NamedQuery(name = "Tipoajuste.findByCuenta", query = "SELECT t FROM Tipoajuste t WHERE t.cuenta = :cuenta"),
    @NamedQuery(name = "Tipoajuste.findByFechaaplicacion", query = "SELECT t FROM Tipoajuste t WHERE t.fechaaplicacion = :fechaaplicacion"),
    @NamedQuery(name = "Tipoajuste.findByUnumero", query = "SELECT t FROM Tipoajuste t WHERE t.numero = :numero")})
public class Tipoajuste implements Serializable {
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
    @Column(name = "cuenta")
    private String cuenta;
    @Column(name = "fechaaplicacion")
    @Temporal(TemporalType.DATE)
    private Date fechaaplicacion;
    @Column(name = "numero")
    private Integer numero;
    @OneToMany(mappedBy = "tipoajuste")
    private List<Cuentas> cuentasList;
    @JoinColumn(name = "tasiento", referencedColumnName = "id")
    @ManyToOne
    private Tipoasiento tasiento;

    public Tipoajuste() {
    }

    public Tipoajuste(Integer id) {
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

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public Date getFechaaplicacion() {
        return fechaaplicacion;
    }

    public void setFechaaplicacion(Date fechaaplicacion) {
        this.fechaaplicacion = fechaaplicacion;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    @XmlTransient
    public List<Cuentas> getCuentasList() {
        return cuentasList;
    }

    public void setCuentasList(List<Cuentas> cuentasList) {
        this.cuentasList = cuentasList;
    }

    public Tipoasiento getTasiento() {
        return tasiento;
    }

    public void setTasiento(Tipoasiento tasiento) {
        this.tasiento = tasiento;
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
        if (!(object instanceof Tipoajuste)) {
            return false;
        }
        Tipoajuste other = (Tipoajuste) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nombre;
    }
    
}
