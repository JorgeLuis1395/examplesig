/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author luis
 */
@Entity
@Table(name = "valesfondosexterior")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Valesfondosexterior.findAll", query = "SELECT v FROM Valesfondosexterior v"),
    @NamedQuery(name = "Valesfondosexterior.findById", query = "SELECT v FROM Valesfondosexterior v WHERE v.id = :id"),
    @NamedQuery(name = "Valesfondosexterior.findByFecha", query = "SELECT v FROM Valesfondosexterior v WHERE v.fecha = :fecha"),
    @NamedQuery(name = "Valesfondosexterior.findByConcepto", query = "SELECT v FROM Valesfondosexterior v WHERE v.concepto = :concepto"),
    @NamedQuery(name = "Valesfondosexterior.findByValor", query = "SELECT v FROM Valesfondosexterior v WHERE v.valor = :valor"),
    @NamedQuery(name = "Valesfondosexterior.findByEstado", query = "SELECT v FROM Valesfondosexterior v WHERE v.estado = :estado"),
    @NamedQuery(name = "Valesfondosexterior.findByNumero", query = "SELECT v FROM Valesfondosexterior v WHERE v.numero = :numero"),
    @NamedQuery(name = "Valesfondosexterior.findByEstablecimiento", query = "SELECT v FROM Valesfondosexterior v WHERE v.establecimiento = :establecimiento"),
    @NamedQuery(name = "Valesfondosexterior.findByPuntoemision", query = "SELECT v FROM Valesfondosexterior v WHERE v.puntoemision = :puntoemision"),
    @NamedQuery(name = "Valesfondosexterior.findByAutorizacion", query = "SELECT v FROM Valesfondosexterior v WHERE v.autorizacion = :autorizacion"),
    @NamedQuery(name = "Valesfondosexterior.findByBaseimponiblecero", query = "SELECT v FROM Valesfondosexterior v WHERE v.baseimponiblecero = :baseimponiblecero"),
    @NamedQuery(name = "Valesfondosexterior.findByBaseimponible", query = "SELECT v FROM Valesfondosexterior v WHERE v.baseimponible = :baseimponible"),
    @NamedQuery(name = "Valesfondosexterior.findByIva", query = "SELECT v FROM Valesfondosexterior v WHERE v.iva = :iva")})
public class Valesfondosexterior implements Serializable {

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
    @Column(name = "concepto")
    private String concepto;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    @Column(name = "estado")
    private Integer estado;
    @Column(name = "numero")
    private Integer numero;
    @Size(max = 2147483647)
    @Column(name = "establecimiento")
    private String establecimiento;
    @Size(max = 2147483647)
    @Column(name = "puntoemision")
    private String puntoemision;
    @Size(max = 2147483647)
    @Column(name = "autorizacion")
    private String autorizacion;
    @Column(name = "baseimponiblecero")
    private BigDecimal baseimponiblecero;
    @Column(name = "baseimponible")
    private BigDecimal baseimponible;
    @Column(name = "iva")
    private BigDecimal iva;
    @JoinColumn(name = "tipodocumento", referencedColumnName = "id")
    @ManyToOne
    private Codigos tipodocumento;
    @JoinColumn(name = "fondo", referencedColumnName = "id")
    @ManyToOne
    private Fondos fondo;
    @JoinColumn(name = "impuesto", referencedColumnName = "id")
    @ManyToOne
    private Impuestos impuesto;
    @JoinColumn(name = "obligacion", referencedColumnName = "id")
    @ManyToOne
    private Obligaciones obligacion;
    @JoinColumn(name = "proveedor", referencedColumnName = "id")
    @ManyToOne
    private Proveedores proveedor;
    @Transient
    private int ajuste;

    public Valesfondosexterior() {
    }

    public Valesfondosexterior(Integer id) {
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

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getEstablecimiento() {
        return establecimiento;
    }

    public void setEstablecimiento(String establecimiento) {
        this.establecimiento = establecimiento;
    }

    public String getPuntoemision() {
        return puntoemision;
    }

    public void setPuntoemision(String puntoemision) {
        this.puntoemision = puntoemision;
    }

    public String getAutorizacion() {
        return autorizacion;
    }

    public void setAutorizacion(String autorizacion) {
        this.autorizacion = autorizacion;
    }

    public BigDecimal getBaseimponiblecero() {
        return baseimponiblecero;
    }

    public void setBaseimponiblecero(BigDecimal baseimponiblecero) {
        this.baseimponiblecero = baseimponiblecero;
    }

    public BigDecimal getBaseimponible() {
        return baseimponible;
    }

    public void setBaseimponible(BigDecimal baseimponible) {
        this.baseimponible = baseimponible;
    }

    public BigDecimal getIva() {
        return iva;
    }

    public void setIva(BigDecimal iva) {
        this.iva = iva;
    }

    public Codigos getTipodocumento() {
        return tipodocumento;
    }

    public void setTipodocumento(Codigos tipodocumento) {
        this.tipodocumento = tipodocumento;
    }

    public Fondos getFondo() {
        return fondo;
    }

    public void setFondo(Fondos fondo) {
        this.fondo = fondo;
    }

    public Impuestos getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(Impuestos impuesto) {
        this.impuesto = impuesto;
    }

    public Obligaciones getObligacion() {
        return obligacion;
    }

    public void setObligacion(Obligaciones obligacion) {
        this.obligacion = obligacion;
    }

    public Proveedores getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedores proveedor) {
        this.proveedor = proveedor;
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
        if (!(object instanceof Valesfondosexterior)) {
            return false;
        }
        Valesfondosexterior other = (Valesfondosexterior) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.sfccbdmq.Valesfondosexterior[ id=" + id + " ]";
    }

    /**
     * @return the ajuste
     */
    public int getAjuste() {
        return ajuste;
    }

    /**
     * @param ajuste the ajuste to set
     */
    public void setAjuste(int ajuste) {
        this.ajuste = ajuste;
    }

}
