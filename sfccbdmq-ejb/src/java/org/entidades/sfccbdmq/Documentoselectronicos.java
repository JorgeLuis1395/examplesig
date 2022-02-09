/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entidades.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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
@Table(name = "documentoselectronicos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Documentoselectronicos.findAll", query = "SELECT d FROM Documentoselectronicos d")
    , @NamedQuery(name = "Documentoselectronicos.findById", query = "SELECT d FROM Documentoselectronicos d WHERE d.id = :id")
    , @NamedQuery(name = "Documentoselectronicos.findByRuc", query = "SELECT d FROM Documentoselectronicos d WHERE d.ruc = :ruc")
    , @NamedQuery(name = "Documentoselectronicos.findByUtilizada", query = "SELECT d FROM Documentoselectronicos d WHERE d.utilizada = :utilizada")
    , @NamedQuery(name = "Documentoselectronicos.findByAutorizacion", query = "SELECT d FROM Documentoselectronicos d WHERE d.autorizacion = :autorizacion")
    , @NamedQuery(name = "Documentoselectronicos.findByClave", query = "SELECT d FROM Documentoselectronicos d WHERE d.clave = :clave")
    , @NamedQuery(name = "Documentoselectronicos.findByXml", query = "SELECT d FROM Documentoselectronicos d WHERE d.xml = :xml")
    , @NamedQuery(name = "Documentoselectronicos.findByTipo", query = "SELECT d FROM Documentoselectronicos d WHERE d.tipo = :tipo")
    , @NamedQuery(name = "Documentoselectronicos.findByFechaemision", query = "SELECT d FROM Documentoselectronicos d WHERE d.fechaemision = :fechaemision")
    , @NamedQuery(name = "Documentoselectronicos.findByValortotal", query = "SELECT d FROM Documentoselectronicos d WHERE d.valortotal = :valortotal")})
public class Documentoselectronicos implements Serializable {

    @Column(name = "baseimponible")
    private BigDecimal baseimponible;
    @Column(name = "baseimponible0")
    private BigDecimal baseimponible0;
    @Column(name = "iva")
    private BigDecimal iva;

    @JoinColumn(name = "cabeccera", referencedColumnName = "id")
    @ManyToOne
    private Cabdocelect cabeccera;
    @JoinColumn(name = "obligacion", referencedColumnName = "id")
    @ManyToOne
    private Obligaciones obligacion;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2147483647)
    @Column(name = "ruc")
    private String ruc;
    @Column(name = "utilizada")
    private Boolean utilizada;
    @Size(max = 2147483647)
    @Column(name = "autorizacion")
    private String autorizacion;
    @Size(max = 2147483647)
    @Column(name = "clave")
    private String clave;
    @Size(max = 2147483647)
    @Column(name = "xml")
    private String xml;
    @Column(name = "tipo")
    private Integer tipo;
    @Column(name = "numero")
    private String numero;
    @Column(name = "fechaemision")
    @Temporal(TemporalType.DATE)
    private Date fechaemision;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "valortotal")
    private BigDecimal valortotal;
    @Transient
    private int ajuste;
    @Transient
    private String  error;
    @Transient
    private boolean  seleccionado;

    public Documentoselectronicos() {
    }

    public Documentoselectronicos(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public Boolean getUtilizada() {
        return utilizada;
    }

    public void setUtilizada(Boolean utilizada) {
        this.utilizada = utilizada;
    }

    public String getAutorizacion() {
        return autorizacion;
    }

    public void setAutorizacion(String autorizacion) {
        this.autorizacion = autorizacion;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

    public Date getFechaemision() {
        return fechaemision;
    }

    public void setFechaemision(Date fechaemision) {
        this.fechaemision = fechaemision;
    }

    public BigDecimal getValortotal() {
        return valortotal;
    }

    public void setValortotal(BigDecimal valortotal) {
        if (valortotal != null) {
            double cuadre = Math.round(valortotal.doubleValue() * 100);
            double dividido = cuadre / 100;
            valortotal = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
        }
        this.valortotal = valortotal;
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
        if (!(object instanceof Documentoselectronicos)) {
            return false;
        }
        Documentoselectronicos other = (Documentoselectronicos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        DecimalFormat df = new DecimalFormat("###,##0.00");
        return clave + " " + sdf.format(fechaemision) + " Valor : " + df.format(valortotal == null ? 0 : valortotal);
    }

    public Cabdocelect getCabeccera() {
        return cabeccera;
    }

    public void setCabeccera(Cabdocelect cabeccera) {
        this.cabeccera = cabeccera;
    }

    public Obligaciones getObligacion() {
        return obligacion;
    }

    public void setObligacion(Obligaciones obligacion) {
        this.obligacion = obligacion;
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
        if (Math.abs(ajuste) > 2) {
            if (ajuste > 2) {
                ajuste = 2;
            } else {
                ajuste = -2;
            }
        }
        this.ajuste = ajuste;
    }

    /**
     * @return the numero
     */
    public String getNumero() {
        return numero;
    }

    /**
     * @param numero the numero to set
     */
    public void setNumero(String numero) {
        this.numero = numero;
    }

    /**
     * @return the error
     */
    public String getError() {
        return error;
    }

    /**
     * @param error the error to set
     */
    public void setError(String error) {
        this.error = error;
    }

    /**
     * @return the seleccionado
     */
    public boolean isSeleccionado() {
        return seleccionado;
    }

    /**
     * @param seleccionado the seleccionado to set
     */
    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

}
