/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@Entity
@Table(name = "retencionescompras")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Retencionescompras.findAll", query = "SELECT r FROM Retencionescompras r")
    , @NamedQuery(name = "Retencionescompras.findById", query = "SELECT r FROM Retencionescompras r WHERE r.id = :id")
    , @NamedQuery(name = "Retencionescompras.findByBaseimponible", query = "SELECT r FROM Retencionescompras r WHERE r.baseimponible = :baseimponible")
    , @NamedQuery(name = "Retencionescompras.findByValor", query = "SELECT r FROM Retencionescompras r WHERE r.valor = :valor")
    , @NamedQuery(name = "Retencionescompras.findByBien", query = "SELECT r FROM Retencionescompras r WHERE r.bien = :bien")
    , @NamedQuery(name = "Retencionescompras.findByPartida", query = "SELECT r FROM Retencionescompras r WHERE r.partida = :partida")
    , @NamedQuery(name = "Retencionescompras.findByBaseimponible0", query = "SELECT r FROM Retencionescompras r WHERE r.baseimponible0 = :baseimponible0")
    , @NamedQuery(name = "Retencionescompras.findByValoriva", query = "SELECT r FROM Retencionescompras r WHERE r.valoriva = :valoriva")
    , @NamedQuery(name = "Retencionescompras.findByIva", query = "SELECT r FROM Retencionescompras r WHERE r.iva = :iva")})
public class Retencionescompras implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "baseimponible")
    private BigDecimal baseimponible;
    @Column(name = "valor")
    private BigDecimal valor;
    @Column(name = "bien")
    private Boolean bien;
    @Size(max = 2147483647)
    @Column(name = "partida")
    private String partida;
    @Column(name = "baseimponible0")
    private BigDecimal baseimponible0;
    @Column(name = "valorprima")
    private BigDecimal valorprima;
    @Column(name = "valoriva")
    private BigDecimal valoriva;
    @Column(name = "iva")
    private BigDecimal iva;
    @JoinColumn(name = "impuesto", referencedColumnName = "id")
    @ManyToOne
    private Impuestos impuesto;
    @JoinColumn(name = "obligacion", referencedColumnName = "id")
    @ManyToOne
    private Obligaciones obligacion;
    @JoinColumn(name = "retencion", referencedColumnName = "id")
    @ManyToOne
    private Retenciones retencion;
    @JoinColumn(name = "retencionimpuesto", referencedColumnName = "id")
    @ManyToOne
    private Retenciones retencionimpuesto;
    @Transient
    private int ajusteRf;
    @Transient
    private int ajusteRi;
    @Transient
    private int ajusteIva;
    @Transient
    private Date fechaRetencion;
    public Retencionescompras() {
    }

    public Retencionescompras(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getBaseimponible() {
        return baseimponible;
    }

    public void setBaseimponible(BigDecimal baseimponible) {
        if (baseimponible != null) {
            double cuadre = Math.round(baseimponible.doubleValue() * 100);
            double dividido = cuadre / 100;
            baseimponible = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
        }
        this.baseimponible = baseimponible;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        if (valor != null) {
            double cuadre = Math.round(valor.doubleValue() * 100);
            double dividido = cuadre / 100;
            valor = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
        }
        this.valor = valor;
    }

    public Boolean getBien() {
        return bien;
    }

    public void setBien(Boolean bien) {
        this.bien = bien;
    }

    public String getPartida() {
        return partida;
    }

    public void setPartida(String partida) {
        this.partida = partida;
    }

    public BigDecimal getBaseimponible0() {
        return baseimponible0;
    }

    public void setBaseimponible0(BigDecimal baseimponible0) {
        if (baseimponible0 != null) {
            double cuadre = Math.round(baseimponible0.doubleValue() * 100);
            double dividido = cuadre / 100;
            baseimponible0 = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
        }
        this.baseimponible0 = baseimponible0;
    }

    public BigDecimal getValoriva() {
        return valoriva;
    }

    public void setValoriva(BigDecimal valoriva) {
        if (valoriva != null) {
            double cuadre = Math.round(valoriva.doubleValue() * 100);
            double dividido = cuadre / 100;
            valoriva = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);

        }
        this.valoriva = valoriva;
    }

    public BigDecimal getIva() {
        return iva;
    }

    public void setIva(BigDecimal iva) {
        if (iva != null) {
            double cuadre = Math.round(iva.doubleValue() * 100);
            double dividido = cuadre / 100;
            iva = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);

        }
        this.iva = iva;
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

    public Retenciones getRetencion() {
        return retencion;
    }

    public void setRetencion(Retenciones retencion) {
        this.retencion = retencion;
    }

    public Retenciones getRetencionimpuesto() {
        return retencionimpuesto;
    }

    public void setRetencionimpuesto(Retenciones retencionimpuesto) {
        this.retencionimpuesto = retencionimpuesto;
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
        if (!(object instanceof Retencionescompras)) {
            return false;
        }
        Retencionescompras other = (Retencionescompras) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.entidades.sfccbdmq.Retencionescompras[ id=" + id + " ]";
    }

    /**
     * @return the ajusteRf
     */
    public int getAjusteRf() {
        return ajusteRf;
    }

    /**
     * @param ajusteRf the ajusteRf to set
     */
    public void setAjusteRf(int ajusteRf) {
        if (Math.abs(ajusteRf) > 2) {
            if (ajusteRf > 2) {
                ajusteRf = 2;
            } else {
                ajusteRf = -2;
            }
        }
        this.ajusteRf = ajusteRf;
    }

    /**
     * @return the ajusteRi
     */
    public int getAjusteRi() {
        return ajusteRi;
    }

    /**
     * @param ajusteRi the ajusteRi to set
     */
    public void setAjusteRi(int ajusteRi) {
        if (Math.abs(ajusteRi) > 2) {
            if (ajusteRi > 2) {
                ajusteRi = 2;
            } else {
                ajusteRi = -2;
            }
        }
        this.ajusteRi = ajusteRi;
    }

    /**
     * @return the ajusteIva
     */
    public int getAjusteIva() {
        return ajusteIva;
    }

    /**
     * @param ajusteIva the ajusteIva to set
     */
    public void setAjusteIva(int ajusteIva) {
        if (Math.abs(ajusteIva) > 2) {
            if (ajusteIva > 2) {
                ajusteIva = 2;
            } else {
                ajusteIva = -2;
            }
        }
        this.ajusteIva = ajusteIva;
    }

    /**
     * @return the fechaRetencion
     */
    public Date getFechaRetencion() {
        return fechaRetencion;
    }

    /**
     * @param fechaRetencion the fechaRetencion to set
     */
    public void setFechaRetencion(Date fechaRetencion) {
        this.fechaRetencion = fechaRetencion;
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
       if (valorprima != null) {
            double cuadre = Math.round(valorprima.doubleValue() * 100);
            double dividido = cuadre / 100;
            valorprima = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
        }
        this.valorprima = valorprima;
    }
}