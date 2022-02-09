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
 * @author edwin
 */
@Entity
@Table(name = "rascompras")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Rascompras.findAll", query = "SELECT r FROM Rascompras r"),
    @NamedQuery(name = "Rascompras.findById", query = "SELECT r FROM Rascompras r WHERE r.id = :id"),
    @NamedQuery(name = "Rascompras.findByCuenta", query = "SELECT r FROM Rascompras r WHERE r.cuenta = :cuenta"),
    @NamedQuery(name = "Rascompras.findByReferencia", query = "SELECT r FROM Rascompras r WHERE r.referencia = :referencia"),
    @NamedQuery(name = "Rascompras.findByValor", query = "SELECT r FROM Rascompras r WHERE r.valor = :valor"),
    @NamedQuery(name = "Rascompras.findByCba", query = "SELECT r FROM Rascompras r WHERE r.cba = :cba"),
    @NamedQuery(name = "Rascompras.findByCc", query = "SELECT r FROM Rascompras r WHERE r.cc = :cc"),
    @NamedQuery(name = "Rascompras.findByValorret", query = "SELECT r FROM Rascompras r WHERE r.valorret = :valorret")})
public class Rascompras implements Serializable {

    @Column(name = "baseimponibleimpuesto")
    private BigDecimal baseimponibleimpuesto;
    @JoinColumn(name = "sustento", referencedColumnName = "id")
    @ManyToOne
    private Codigos sustento;
    @JoinColumn(name = "notacredito", referencedColumnName = "id")
    @ManyToOne
    private Notascredito notacredito;
    @Column(name = "valorimpuesto")
    private BigDecimal valorimpuesto;
    @Column(name = "valorprima")
    private BigDecimal valorprima;
    @Column(name = "vretimpuesto")
    private BigDecimal vretimpuesto;
    @JoinColumn(name = "retimpuesto", referencedColumnName = "id")
    @ManyToOne
    private Retenciones retimpuesto;
    @JoinColumn(name = "detallecompromiso", referencedColumnName = "id")
    @ManyToOne
    private Detallecompromiso detallecompromiso;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "cuenta")
    private String cuenta;
    @Size(max = 2147483647)
    @Column(name = "referencia")
    private String referencia;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valor")
    private BigDecimal valor;
    @Size(max = 2)
    @Column(name = "cba")
    private String cba;
    @Column(name = "bien")
    private Boolean bien;
    
    @Column(name = "pagado")
     @Temporal(TemporalType.DATE)
    private Date pagado;
    @Size(max = 2147483647)
    @Column(name = "cc")
    private String cc;
    @Column(name = "valorret")
    private BigDecimal valorret;
    @JoinColumn(name = "tipoegreso", referencedColumnName = "id")
    @ManyToOne
    private Tipoegreso tipoegreso;
    @JoinColumn(name = "retencion", referencedColumnName = "id")
    @ManyToOne
    private Retenciones retencion;
    @JoinColumn(name = "obligacion", referencedColumnName = "id")
    @ManyToOne
    private Obligaciones obligacion;
    @JoinColumn(name = "impuesto", referencedColumnName = "id")
    @ManyToOne
    private Impuestos impuesto;
    @JoinColumn(name = "idcuenta", referencedColumnName = "id")
    @ManyToOne
    private Cuentas idcuenta;
    @JoinColumn(name = "anticipo", referencedColumnName = "id")
    @ManyToOne
    private Anticipos anticipo;
    @Transient
    private int numero;
    @Transient
    private boolean selecciona;
    public Rascompras() {
    }

    public Rascompras(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getCba() {
        return cba;
    }

    public void setCba(String cba) {
        this.cba = cba;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public BigDecimal getValorret() {
        return valorret;
    }

    public void setValorret(BigDecimal valorret) {
        this.valorret = valorret;
    }

    public Tipoegreso getTipoegreso() {
        return tipoegreso;
    }

    public void setTipoegreso(Tipoegreso tipoegreso) {
        this.tipoegreso = tipoegreso;
    }

    public Retenciones getRetencion() {
        return retencion;
    }

    public void setRetencion(Retenciones retencion) {
        this.retencion = retencion;
    }

    public Obligaciones getObligacion() {
        return obligacion;
    }

    public void setObligacion(Obligaciones obligacion) {
        this.obligacion = obligacion;
    }

    public Impuestos getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(Impuestos impuesto) {
        this.impuesto = impuesto;
    }

    public Cuentas getIdcuenta() {
        return idcuenta;
    }

    public void setIdcuenta(Cuentas idcuenta) {
        this.idcuenta = idcuenta;
    }

    public Anticipos getAnticipo() {
        return anticipo;
    }

    public void setAnticipo(Anticipos anticipo) {
        this.anticipo = anticipo;
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
        if (!(object instanceof Rascompras)) {
            return false;
        }
        Rascompras other = (Rascompras) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sigaf.entidades.Rascompras[ id=" + id + " ]";
    }

    /**
     * @return the numero
     */
    public int getNumero() {
        return numero;
    }

    /**
     * @param numero the numero to set
     */
    public void setNumero(int numero) {
        this.numero = numero;
    }

    /**
     * @return the bien
     */
    public Boolean getBien() {
        return bien;
    }

    /**
     * @param bien the bien to set
     */
    public void setBien(Boolean bien) {
        this.bien = bien;
    }

    public Detallecompromiso getDetallecompromiso() {
        return detallecompromiso;
    }

    public void setDetallecompromiso(Detallecompromiso detallecompromiso) {
        this.detallecompromiso = detallecompromiso;
    }

    public BigDecimal getValorimpuesto() {
        return valorimpuesto;
    }

    public void setValorimpuesto(BigDecimal valorimpuesto) {
        this.valorimpuesto = valorimpuesto;
    }

    public BigDecimal getVretimpuesto() {
        return vretimpuesto;
    }

    public void setVretimpuesto(BigDecimal vretimpuesto) {
        this.vretimpuesto = vretimpuesto;
    }

    public Retenciones getRetimpuesto() {
        return retimpuesto;
    }

    public void setRetimpuesto(Retenciones retimpuesto) {
        this.retimpuesto = retimpuesto;
    }

    public Notascredito getNotacredito() {
        return notacredito;
    }

    public void setNotacredito(Notascredito notacredito) {
        this.notacredito = notacredito;
    }

    /**
     * @return the pagado
     */
    public Date getPagado() {
        return pagado;
    }

    /**
     * @param pagado the pagado to set
     */
    public void setPagado(Date pagado) {
        this.pagado = pagado;
    }

    /**
     * @return the valorprima
     */
    public BigDecimal getValorprima() {
        return valorprima;
    }

    /**
     * @param valorprima the valorprima to set
     */
    public void setValorprima(BigDecimal valorprima) {
        this.valorprima = valorprima;
    }

    public BigDecimal getBaseimponibleimpuesto() {
        return baseimponibleimpuesto;
    }

    public void setBaseimponibleimpuesto(BigDecimal baseimponibleimpuesto) {
        this.baseimponibleimpuesto = baseimponibleimpuesto;
    }

    public Codigos getSustento() {
        return sustento;
    }

    public void setSustento(Codigos sustento) {
        this.sustento = sustento;
    }

    /**
     * @return the selecciona
     */
    public boolean isSelecciona() {
        return selecciona;
    }

    /**
     * @param selecciona the selecciona to set
     */
    public void setSelecciona(boolean selecciona) {
        this.selecciona = selecciona;
    }
    
}
