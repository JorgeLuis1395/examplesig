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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
@Table(name = "notascredito")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Notascredito.findAll", query = "SELECT n FROM Notascredito n"),
    @NamedQuery(name = "Notascredito.findById", query = "SELECT n FROM Notascredito n WHERE n.id = :id"),
    @NamedQuery(name = "Notascredito.findByFecha", query = "SELECT n FROM Notascredito n WHERE n.fecha = :fecha"),
    @NamedQuery(name = "Notascredito.findByEmision", query = "SELECT n FROM Notascredito n WHERE n.emision = :emision"),
    @NamedQuery(name = "Notascredito.findByContabilizacion", query = "SELECT n FROM Notascredito n WHERE n.contabilizacion = :contabilizacion"),
    @NamedQuery(name = "Notascredito.findByPunto", query = "SELECT n FROM Notascredito n WHERE n.punto = :punto"),
    @NamedQuery(name = "Notascredito.findByEstablecimiento", query = "SELECT n FROM Notascredito n WHERE n.establecimiento = :establecimiento"),
    @NamedQuery(name = "Notascredito.findByNumero", query = "SELECT n FROM Notascredito n WHERE n.numero = :numero"),
    @NamedQuery(name = "Notascredito.findByValor", query = "SELECT n FROM Notascredito n WHERE n.valor = :valor")})
public class Notascredito implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Column(name = "emision")
    @Temporal(TemporalType.DATE)
    private Date emision;
    @Column(name = "contabilizacion")
    @Temporal(TemporalType.DATE)
    private Date contabilizacion;
    @Size(max = 4)
    @Column(name = "punto")
    private String punto;
    @Column(name = "concepto")
    private String concepto;
    @Column(name = "autorizacion")
    private String autorizacion;
    @Size(max = 4)
    @Column(name = "establecimiento")
    private String establecimiento;
    @Size(max = 2147483647)
    @Column(name = "numero")
    private String numero;
    @Column(name = "electronico")
    private String electronico;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    @OneToMany(mappedBy = "notacredito")
    private List<Nckardex> nckardexList;
    @OneToMany(mappedBy = "notacredito")
    private List<Rascompras> rascomprasList;
    @JoinColumn(name = "obligacion", referencedColumnName = "id")
    @OneToOne
    private Obligaciones obligacion;
    @Transient
    private double ocupado;
    @Transient
    private double saldo;

    public Notascredito() {
    }

    public Notascredito(Integer id) {
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

    public Date getEmision() {
        return emision;
    }

    public void setEmision(Date emision) {
        this.emision = emision;
    }

    public Date getContabilizacion() {
        return contabilizacion;
    }

    public void setContabilizacion(Date contabilizacion) {
        this.contabilizacion = contabilizacion;
    }

    public String getPunto() {
        return punto;
    }

    public void setPunto(String punto) {
        this.punto = punto;
    }

    public String getEstablecimiento() {
        return establecimiento;
    }

    public void setEstablecimiento(String establecimiento) {
        this.establecimiento = establecimiento;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    @XmlTransient
    public List<Nckardex> getNckardexList() {
        return nckardexList;
    }

    public void setNckardexList(List<Nckardex> nckardexList) {
        this.nckardexList = nckardexList;
    }

    @XmlTransient
    public List<Rascompras> getRascomprasList() {
        return rascomprasList;
    }

    public void setRascomprasList(List<Rascompras> rascomprasList) {
        this.rascomprasList = rascomprasList;
    }

    public Obligaciones getObligacion() {
        return obligacion;
    }

    public void setObligacion(Obligaciones obligacion) {
        this.obligacion = obligacion;
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
        if (!(object instanceof Notascredito)) {
            return false;
        }
        Notascredito other = (Notascredito) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Notascredito[ id=" + id + " ]";
    }

    /**
     * @return the concepto
     */
    public String getConcepto() {
        return concepto;
    }

    /**
     * @param concepto the concepto to set
     */
    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    /**
     * @return the autorizacion
     */
    public String getAutorizacion() {
        return autorizacion;
    }

    /**
     * @param autorizacion the autorizacion to set
     */
    public void setAutorizacion(String autorizacion) {
        this.autorizacion = autorizacion;
    }

    /**
     * @return the electronico
     */
    public String getElectronico() {
        return electronico;
    }

    /**
     * @param electronico the electronico to set
     */
    public void setElectronico(String electronico) {
        this.electronico = electronico;
    }

    /**
     * @return the ocupado
     */
    public double getOcupado() {
        return ocupado;
    }

    /**
     * @param ocupado the ocupado to set
     */
    public void setOcupado(double ocupado) {
        this.ocupado = ocupado;
    }

    /**
     * @return the saldo
     */
    public double getSaldo() {
        return saldo;
    }

    /**
     * @param saldo the saldo to set
     */
    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }
    
}
