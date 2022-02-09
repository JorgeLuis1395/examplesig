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
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author luis
 */
@Entity
@Table(name = "detalleconciliaciones")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Detalleconciliaciones.findAll", query = "SELECT d FROM Detalleconciliaciones d"),
    @NamedQuery(name = "Detalleconciliaciones.findById", query = "SELECT d FROM Detalleconciliaciones d WHERE d.id = :id"),
    @NamedQuery(name = "Detalleconciliaciones.findByValor", query = "SELECT d FROM Detalleconciliaciones d WHERE d.valor = :valor"),
    @NamedQuery(name = "Detalleconciliaciones.findByFecha", query = "SELECT d FROM Detalleconciliaciones d WHERE d.fecha = :fecha"),
    @NamedQuery(name = "Detalleconciliaciones.findBySpi", query = "SELECT d FROM Detalleconciliaciones d WHERE d.spi = :spi"),
    @NamedQuery(name = "Detalleconciliaciones.findByDescripcion", query = "SELECT d FROM Detalleconciliaciones d WHERE d.descripcion = :descripcion"),
    @NamedQuery(name = "Detalleconciliaciones.findByReferencia", query = "SELECT d FROM Detalleconciliaciones d WHERE d.referencia = :referencia"),
    @NamedQuery(name = "Detalleconciliaciones.findByOficio", query = "SELECT d FROM Detalleconciliaciones d WHERE d.oficio = :oficio"),
    @NamedQuery(name = "Detalleconciliaciones.findByComprobante", query = "SELECT d FROM Detalleconciliaciones d WHERE d.comprobante = :comprobante"),
    @NamedQuery(name = "Detalleconciliaciones.findByConcepto", query = "SELECT d FROM Detalleconciliaciones d WHERE d.concepto = :concepto"),
    @NamedQuery(name = "Detalleconciliaciones.findByTipo", query = "SELECT d FROM Detalleconciliaciones d WHERE d.tipo = :tipo")})
public class Detalleconciliaciones implements Serializable {

    @OneToMany(mappedBy = "detalleconciliacion")
    private List<Renglonescon> renglonesconList;
    @OneToMany(mappedBy = "detalleconciliacion")
    private List<Renglones> renglonesList;
//    @OneToMany(mappedBy = "conciliacion")
//    private List<Renglones> renglonesList;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Size(max = 2147483647)
    @Column(name = "spi")
    private String spi;
    @Size(max = 2147483647)
    @Column(name = "descripcion")
    private String descripcion;
    @Size(max = 2147483647)
    @Column(name = "referencia")
    private String referencia;
    @Size(max = 2147483647)
    @Column(name = "oficio")
    private String oficio;
    @Size(max = 2147483647)
    @Column(name = "comprobante")
    private String comprobante;
    @Size(max = 2147483647)
    @Column(name = "concepto")
    private String concepto;
    @Column(name = "listo")
    private Boolean listo;
    @Size(max = 2147483647)
    @Column(name = "tipo")
    private String tipo;
    @Column(name = "renglonid")
    private Integer renglonid;
    @Column(name = "rengloncid")
    private Integer rengloncid;
    @JoinColumn(name = "conciliacion", referencedColumnName = "id")
    @ManyToOne
    private Conciliaciones conciliacion;
    @OneToMany(mappedBy = "detalle")
    private List<Rasconciliaciones> rasconciliacionesList;
    @Transient
    private boolean seleccionar;
    @Transient
    private double debitos;

    public Detalleconciliaciones() {
    }

    public Detalleconciliaciones(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getSpi() {
        return spi;
    }

    public void setSpi(String spi) {
        this.spi = spi;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getOficio() {
        return oficio;
    }

    public void setOficio(String oficio) {
        this.oficio = oficio;
    }

    public String getComprobante() {
        return comprobante;
    }

    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Conciliaciones getConciliacion() {
        return conciliacion;
    }

    public void setConciliacion(Conciliaciones conciliacion) {
        this.conciliacion = conciliacion;
    }

    @XmlTransient
    public List<Rasconciliaciones> getRasconciliacionesList() {
        return rasconciliacionesList;
    }

    public void setRasconciliacionesList(List<Rasconciliaciones> rasconciliacionesList) {
        this.rasconciliacionesList = rasconciliacionesList;
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
        if (!(object instanceof Detalleconciliaciones)) {
            return false;
        }
        Detalleconciliaciones other = (Detalleconciliaciones) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Detalleconciliaciones[ id=" + id + " ]";
    }

    /**
     * @return the seleccionar
     */
    public boolean isSeleccionar() {
        return seleccionar;
    }

    /**
     * @param seleccionar the seleccionar to set
     */
    public void setSeleccionar(boolean seleccionar) {
        this.seleccionar = seleccionar;
    }

//    @XmlTransient
//    public List<Renglones> getRenglonesList() {
//        return renglonesList;
//    }
//
//    public void setRenglonesList(List<Renglones> renglonesList) {
//        this.renglonesList = renglonesList;
//    }

    /**
     * @return the debitos
     */
    public double getDebitos() {
        return debitos;
    }

    /**
     * @param debitos the debitos to set
     */
    public void setDebitos(double debitos) {
        this.debitos = debitos;
    }

    /**
     * @return the listo
     */
    public Boolean getListo() {
        return listo;
    }

    /**
     * @param listo the listo to set
     */
    public void setListo(Boolean listo) {
        this.listo = listo;
    }

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

    /**
     * @return the renglonid
     */
    public Integer getRenglonid() {
        return renglonid;
    }

    /**
     * @param renglonid the renglonid to set
     */
    public void setRenglonid(Integer renglonid) {
        this.renglonid = renglonid;
    }

    /**
     * @return the rengloncid
     */
    public Integer getRengloncid() {
        return rengloncid;
    }

    /**
     * @param rengloncid the rengloncid to set
     */
    public void setRengloncid(Integer rengloncid) {
        this.rengloncid = rengloncid;
    }

}
