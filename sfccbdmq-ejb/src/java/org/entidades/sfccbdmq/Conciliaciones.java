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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author luis
 */
@Entity
@Table(name = "conciliaciones")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Conciliaciones.findAll", query = "SELECT c FROM Conciliaciones c"),
    @NamedQuery(name = "Conciliaciones.findById", query = "SELECT c FROM Conciliaciones c WHERE c.id = :id"),
    @NamedQuery(name = "Conciliaciones.findByFecha", query = "SELECT c FROM Conciliaciones c WHERE c.fecha = :fecha"),
    @NamedQuery(name = "Conciliaciones.findByObservaciones", query = "SELECT c FROM Conciliaciones c WHERE c.observaciones = :observaciones"),
    @NamedQuery(name = "Conciliaciones.findByUsuario", query = "SELECT c FROM Conciliaciones c WHERE c.usuario = :usuario"),
    @NamedQuery(name = "Conciliaciones.findByTerminado", query = "SELECT c FROM Conciliaciones c WHERE c.terminado = :terminado")})
public class Conciliaciones implements Serializable {

    @OneToMany(mappedBy = "conciliacion")
    private List<Renglonescon> renglonesconList;
    @OneToMany(mappedBy = "conciliacion")
    private List<Renglones> renglonesList;
//    @OneToMany(mappedBy = "conciliacion")
//    private List<Renglones> renglonesList;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "saldobanco")
    private BigDecimal saldobanco;
    @Column(name = "saldocuenta")
    private BigDecimal saldocuenta;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Size(max = 2147483647)
    @Column(name = "observaciones")
    private String observaciones;
    @Size(max = 2147483647)
    @Column(name = "usuario")
    private String usuario;
    @Column(name = "terminado")
    private Boolean terminado;
    @OneToMany(mappedBy = "conciliacion")
    private List<Detalleconciliaciones> detalleconciliacionesList;
//    @OneToMany(mappedBy = "conciliacion")
//    private List<Renglones> renglonesList;
    @JoinColumn(name = "banco", referencedColumnName = "id")
    @ManyToOne
    private Bancos banco;

    public Conciliaciones() {
    }

    public Conciliaciones(Integer id) {
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

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Boolean getTerminado() {
        return terminado;
    }

    public void setTerminado(Boolean terminado) {
        this.terminado = terminado;
    }

    @XmlTransient
    public List<Detalleconciliaciones> getDetalleconciliacionesList() {
        return detalleconciliacionesList;
    }

    public void setDetalleconciliacionesList(List<Detalleconciliaciones> detalleconciliacionesList) {
        this.detalleconciliacionesList = detalleconciliacionesList;
    }

//    @XmlTransient
//    public List<Renglones> getRenglonesList() {
//        return renglonesList;
//    }
//
//    public void setRenglonesList(List<Renglones> renglonesList) {
//        this.renglonesList = renglonesList;
//    }

    public Bancos getBanco() {
        return banco;
    }

    public void setBanco(Bancos banco) {
        this.banco = banco;
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
        if (!(object instanceof Conciliaciones)) {
            return false;
        }
        Conciliaciones other = (Conciliaciones) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Conciliaciones[ id=" + id + " ]";
    }

    public BigDecimal getSaldobanco() {
        return saldobanco;
    }

    public void setSaldobanco(BigDecimal saldobanco) {
        this.saldobanco = saldobanco;
    }

    public BigDecimal getSaldocuenta() {
        return saldocuenta;
    }

    public void setSaldocuenta(BigDecimal saldocuenta) {
        this.saldocuenta = saldocuenta;
    }

//    @XmlTransient
//    public List<Renglones> getRenglonesList() {
//        return renglonesList;
//    }
//
//    public void setRenglonesList(List<Renglones> renglonesList) {
//        this.renglonesList = renglonesList;
//    }
//    

    @XmlTransient
    public List<Renglonescon> getRenglonesconList() {
        return renglonesconList;
    }

    public void setRenglonesconList(List<Renglonescon> renglonesconList) {
        this.renglonesconList = renglonesconList;
    }

    @XmlTransient
    public List<Renglones> getRenglonesList() {
        return renglonesList;
    }

    public void setRenglonesList(List<Renglones> renglonesList) {
        this.renglonesList = renglonesList;
    }
}
