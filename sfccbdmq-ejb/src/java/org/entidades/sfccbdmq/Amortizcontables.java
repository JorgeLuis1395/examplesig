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
@Table(name = "amortizcontables")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Amortizcontables.findAll", query = "SELECT a FROM Amortizcontables a"),
    @NamedQuery(name = "Amortizcontables.findById", query = "SELECT a FROM Amortizcontables a WHERE a.id = :id"),
    @NamedQuery(name = "Amortizcontables.findByFecha", query = "SELECT a FROM Amortizcontables a WHERE a.fecha = :fecha"),
    @NamedQuery(name = "Amortizcontables.findByCtadebito", query = "SELECT a FROM Amortizcontables a WHERE a.ctadebito = :ctadebito"),
    @NamedQuery(name = "Amortizcontables.findByCtacredito", query = "SELECT a FROM Amortizcontables a WHERE a.ctacredito = :ctacredito"),
    @NamedQuery(name = "Amortizcontables.findByMotivo", query = "SELECT a FROM Amortizcontables a WHERE a.motivo = :motivo"),
    @NamedQuery(name = "Amortizcontables.findByDias", query = "SELECT a FROM Amortizcontables a WHERE a.dias = :dias"),
    @NamedQuery(name = "Amortizcontables.findByFinalizado", query = "SELECT a FROM Amortizcontables a WHERE a.finalizado = :finalizado")})
public class Amortizcontables implements Serializable {
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
    @Column(name = "ctadebito")
    private String ctadebito;
    @Size(max = 2147483647)
    @Column(name = "ctacredito")
    private String ctacredito;
    @Size(max = 2147483647)
    @Column(name = "motivo")
    private String motivo;
    @Column(name = "dias")
    private Integer dias;
    @Column(name = "finalizado")
    @Temporal(TemporalType.DATE)
    private Date finalizado;
    @OneToMany(mappedBy = "amortizcontab")
    private List<Detalleamortiz> detalleamortizList;
    @OneToMany(mappedBy = "amortizacion")
    private List<Obligaciones> obligacionesList;

    public Amortizcontables() {
    }

    public Amortizcontables(Integer id) {
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

    public String getCtadebito() {
        return ctadebito;
    }

    public void setCtadebito(String ctadebito) {
        this.ctadebito = ctadebito;
    }

    public String getCtacredito() {
        return ctacredito;
    }

    public void setCtacredito(String ctacredito) {
        this.ctacredito = ctacredito;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Integer getDias() {
        return dias;
    }

    public void setDias(Integer dias) {
        this.dias = dias;
    }

    public Date getFinalizado() {
        return finalizado;
    }

    public void setFinalizado(Date finalizado) {
        this.finalizado = finalizado;
    }

    @XmlTransient
    public List<Detalleamortiz> getDetalleamortizList() {
        return detalleamortizList;
    }

    public void setDetalleamortizList(List<Detalleamortiz> detalleamortizList) {
        this.detalleamortizList = detalleamortizList;
    }

    @XmlTransient
    public List<Obligaciones> getObligacionesList() {
        return obligacionesList;
    }

    public void setObligacionesList(List<Obligaciones> obligacionesList) {
        this.obligacionesList = obligacionesList;
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
        if (!(object instanceof Amortizcontables)) {
            return false;
        }
        Amortizcontables other = (Amortizcontables) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Amortizcontables[ id=" + id + " ]";
    }
    
}
