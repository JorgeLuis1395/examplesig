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
@Table(name = "tipomovactivos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tipomovactivos.findAll", query = "SELECT t FROM Tipomovactivos t"),
    @NamedQuery(name = "Tipomovactivos.findById", query = "SELECT t FROM Tipomovactivos t WHERE t.id = :id"),
    @NamedQuery(name = "Tipomovactivos.findByDescripcion", query = "SELECT t FROM Tipomovactivos t WHERE t.descripcion = :descripcion"),
    @NamedQuery(name = "Tipomovactivos.findByCuenta", query = "SELECT t FROM Tipomovactivos t WHERE t.cuenta = :cuenta"),
    @NamedQuery(name = "Tipomovactivos.findByFecha", query = "SELECT t FROM Tipomovactivos t WHERE t.fecha = :fecha"),
    @NamedQuery(name = "Tipomovactivos.findByContabiliza", query = "SELECT t FROM Tipomovactivos t WHERE t.contabiliza = :contabiliza"),
    @NamedQuery(name = "Tipomovactivos.findByIngreso", query = "SELECT t FROM Tipomovactivos t WHERE t.ingreso = :ingreso"),
    @NamedQuery(name = "Tipomovactivos.findByTipo", query = "SELECT t FROM Tipomovactivos t WHERE t.tipo = :tipo")})
public class Tipomovactivos implements Serializable {
    @Column(name = "ultimo")
    private Integer ultimo;
    @JoinColumn(name = "tipoasientocontrol", referencedColumnName = "id")
    @ManyToOne
    private Tipoasiento tipoasientocontrol;
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
    @Column(name = "debito")
    private String debito;
    @Column(name = "cuenta1")
    private String cuenta1;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Column(name = "contabiliza")
    private Boolean contabiliza;
    @Column(name = "ingreso")
    private Boolean ingreso;
    @Column(name = "tipo")
    private Integer tipo;
    @OneToMany(mappedBy = "baja")
    private List<Activos> activosList;
    @OneToMany(mappedBy = "alta")
    private List<Activos> activosList1;
    @JoinColumn(name = "tipoasiento", referencedColumnName = "id")
    @ManyToOne
    private Tipoasiento tipoasiento;

    public Tipomovactivos() {
    }

    public Tipomovactivos(Integer id) {
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

    public Boolean getContabiliza() {
        return contabiliza;
    }

    public void setContabiliza(Boolean contabiliza) {
        this.contabiliza = contabiliza;
    }

    public Boolean getIngreso() {
        return ingreso;
    }

    public void setIngreso(Boolean ingreso) {
        this.ingreso = ingreso;
    }

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

    @XmlTransient
    public List<Activos> getActivosList() {
        return activosList;
    }

    public void setActivosList(List<Activos> activosList) {
        this.activosList = activosList;
    }

    @XmlTransient
    public List<Activos> getActivosList1() {
        return activosList1;
    }

    public void setActivosList1(List<Activos> activosList1) {
        this.activosList1 = activosList1;
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
        if (!(object instanceof Tipomovactivos)) {
            return false;
        }
        Tipomovactivos other = (Tipomovactivos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return descripcion;
    }

    /**
     * @return the debito
     */
    public String getDebito() {
        return debito;
    }

    /**
     * @param debito the debito to set
     */
    public void setDebito(String debito) {
        this.debito = debito;
    }

    /**
     * @return the cuenta1
     */
    public String getCuenta1() {
        return cuenta1;
    }

    /**
     * @param cuenta1 the cuenta1 to set
     */
    public void setCuenta1(String cuenta1) {
        this.cuenta1 = cuenta1;
    }

    public Tipoasiento getTipoasientocontrol() {
        return tipoasientocontrol;
    }

    public void setTipoasientocontrol(Tipoasiento tipoasientocontrol) {
        this.tipoasientocontrol = tipoasientocontrol;
    }

    public Integer getUltimo() {
        return ultimo;
    }

    public void setUltimo(Integer ultimo) {
        this.ultimo = ultimo;
    }
    
}
